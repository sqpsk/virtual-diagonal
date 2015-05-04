#include "wavefile.h"

#include <fstream>
#include <iostream>

bool hasFmtExtensionSizeField(const FmtChunk& fmt)
{
	return fmt.size > 16;
}

bool hasFmtExtensionFields(const FmtChunk& fmt)
{
	return hasFmtExtensionSizeField(fmt) && fmt.cbSize >= 22;
}

bool hasFmtExtensionSizeField(const WaveHeader& wav)
{
	return hasFmtExtensionSizeField(wav.fmt);
}

bool hasFmtExtensionFields(const WaveHeader& wav)
{
	return hasFmtExtensionFields(wav.fmt);
}

bool hasFactChunk(const WaveHeader& wf)
{
	return wf.fact.id == FactChunk::ID;
}

namespace {

template <typename T> inline 
void read(std::istream& in, T& t)
{
	in.read(reinterpret_cast<char*>(&t), sizeof (T));
}

template <typename T> inline 
void write(std::ostream& out, T t)
{
	out.write(reinterpret_cast<char*>(&t), sizeof (T));
}

int extensionSizeBytes(const FmtChunk& fmt)
{
	int size = FmtChunk::sizeBytes;
	if (hasFmtExtensionSizeField(fmt))
		size += 2;
	if (hasFmtExtensionFields(fmt))
		size += fmt.cbSize;
	return size;
}

void readFormatChunk(std::istream& in, FmtChunk& fmt)
{
	read(in, fmt.id);
	read(in, fmt.size);
	read(in, fmt.wFormatTag);
	read(in, fmt.nChannels);
	read(in, fmt.nSamplesPerSec);
	read(in, fmt.nAvgBytesPerSec);
	read(in, fmt.nBlockAlign);
	read(in, fmt.wBitsPerSample);

	if (hasFmtExtensionSizeField(fmt))
	{
		read(in, fmt.cbSize);
		int skip;
		if (hasFmtExtensionFields(fmt))
		{
			read(in, fmt.extension.wValidBitsPerSample);
			read(in, fmt.extension.dwChannelMask);
			read(in, fmt.extension.GUID);
			skip = fmt.cbSize - FmtExtensionFields::sizeBytes;
		} 
		else
		{
			skip = fmt.cbSize;
		}
		// Skip what remains of the extension fields
		if (skip > 0)
			in.seekg(skip, std::ios::cur);
	}
}

} // end namespace


void readWaveHeader(std::istream& in, WaveHeader& wav)
{
	// TODO minimum size check
	read(in, wav.id);
	read(in, wav.size);
	read(in, wav.waveId);
	readFormatChunk(in, wav.fmt);

	uint32_t id;
	read(in, id);
	if (id == FactChunk::ID)
	{
		wav.fact.id = id;
		read(in, wav.fact.size);
		read(in, wav.fact.dwSampleLength);
		read(in, wav.data.id);
	}
	else
	{
		wav.data.id = id;
	}
	read(in, wav.data.size);
}

void readWaveHeader(const char* filename, WaveHeader& waveFile)
{
	std::ifstream ifs(filename);
	readWaveHeader(ifs, waveFile);
}

size_t dataOffsetBytes(const WaveHeader& wav)
{
	static const int masterRiffChunkSize = 8 + 4;
	static const int fmtChunkSize = 8 + 16;
	static const int factChunkSize = 8 + 4;

	int size = masterRiffChunkSize + fmtChunkSize;
	if (hasFmtExtensionSizeField(wav))
		size += 2;
	if (hasFmtExtensionFields(wav))
		size += wav.fmt.cbSize;

	if (hasFactChunk(wav))
		size += factChunkSize;

	// Data chunk header
	size += 8;

	return size;
}

void writeFormatChunk(std::ostream& out, FmtChunk& fmt)
{
	write(out, fmt.id);
	write(out, fmt.size);
	write(out, fmt.wFormatTag);
	write(out, fmt.nChannels);
	write(out, fmt.nSamplesPerSec);
	write(out, fmt.nAvgBytesPerSec);
	write(out, fmt.nBlockAlign);
	write(out, fmt.wBitsPerSample);

	if (hasFmtExtensionSizeField(fmt))
	{
		write(out, fmt.cbSize);
		if (hasFmtExtensionFields(fmt))
		{
			write(out, fmt.extension.wValidBitsPerSample);
			write(out, fmt.extension.dwChannelMask);
			write(out, fmt.extension.GUID);
		}
	}
}

void writeWaveHeader(std::ostream& out, WaveHeader& wav)
{
	write(out, wav.id);
	write(out, wav.size);
	write(out, wav.waveId);

	writeFormatChunk(out, wav.fmt);

	if (hasFactChunk(wav))
	{
		write(out, wav.fact.id);
		write(out, wav.fact.size);
		write(out, wav.fact.dwSampleLength);
	}

	write(out, wav.data.id);
	write(out, wav.data.size);
}
