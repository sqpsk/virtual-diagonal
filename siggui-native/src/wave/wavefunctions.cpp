#include "wavefunctions.h"

#include <cstdlib>
#include <iostream>
#include <iomanip>
#include <limits>
#include <string>
#include "wave/wavefile.h"

namespace {

template <typename T> struct PrintHexImpl
{
	PrintHexImpl(T t) : _t(t) { }
	const T _t;
};

template <typename T> PrintHexImpl<T> printHex(T t) { return PrintHexImpl<T>(t); }

template <typename T>
std::ostream& operator<<(std::ostream& out, const PrintHexImpl<T>& p)
{
	return out << "0x" << std::hex << p._t << std::dec;
}

template <typename T> struct PrintStringImpl
{
	PrintStringImpl(T t) : _t(t) { }
	const T _t;
};

template <typename T> 
PrintStringImpl<T> printString(T t) { return PrintStringImpl<T>(t); }

template <typename T>
std::ostream& operator<<(std::ostream& out, const PrintStringImpl<T>& p)
{
	return out << std::string(reinterpret_cast<const char*>(&p._t), sizeof (T));
}

void printHexBytes(const uint8_t* begin, const uint8_t* end, std::ostream& out)
{
	for (const uint8_t* i = begin; i != end; ++i)
		out << std::setw(2) << std::setfill('0') << int(*i);
}

} // end namespace

void printRiffChunkHeader(const RiffChunk& c, const char* prefix, std::ostream& out)
{
	out << prefix << "ID " << printString(c.id) << " (" << printHex(c.id) << ")\n";
	out << prefix << "Size " << c.size << "\n";
}

void printGuid(const uint8_t guid[16], std::ostream& out)
{
	out << std::hex;
	printHexBytes(guid, guid + 4, out);
	out << "-";
	printHexBytes(guid + 4, guid + 6, out);
	out << "-";
	printHexBytes(guid + 6, guid + 8, out);
	out << "-";
	printHexBytes(guid + 8, guid + 10, out);
	out << "-";
	printHexBytes(guid + 10, guid + 16, out);
	out << std::dec;
}

void printWaveHeader(const WaveHeader& wav, std::ostream& out)
{
	printRiffChunkHeader(wav, "", out);    
	out << "\tWAVEID " << printString(wav.waveId) << " (" << printHex(wav.waveId) << ")\n";

	printRiffChunkHeader(wav.fmt, "\t", out);
	out << "\t\tSample format " << wav.fmt.wFormatTag << " (" << printHex(wav.fmt.wFormatTag) << ")\n";
	out << "\t\tChannels " << wav.fmt.nChannels << "\n";
	out << "\t\tSample rate " << wav.fmt.nSamplesPerSec << "\n";
	out << "\t\tByte rate " << wav.fmt.nAvgBytesPerSec << "\n";
	out << "\t\tBlock align " << wav.fmt.nBlockAlign << "\n";
	out << "\t\tBits per sample " << wav.fmt.wBitsPerSample << "\n";

	if (hasFmtExtensionSizeField(wav))
	{
		out << "\t\tExtension size " << wav.fmt.cbSize << "\n";
		if (hasFmtExtensionFields(wav))
		{
			out << "\t\tChannel mask " << printHex(wav.fmt.extension.dwChannelMask) << "\n";
			out << "\t\tValid bits per sample " << wav.fmt.extension.wValidBitsPerSample << "\n";
			out << "\t\tGUID ";
			printGuid(wav.fmt.extension.GUID, out);
			out << "\n";
		}
	}

	if (hasFactChunk(wav))
	{
		printRiffChunkHeader(wav.fact, "\t", out);
		out << "\t\tSamples per channel " << wav.fact.dwSampleLength << "\n";
	}

	printRiffChunkHeader(wav.data, "\t", out);   
}
