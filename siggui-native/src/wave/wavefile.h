#ifndef WAVE_FILE_H
#define	WAVE_FILE_H

#include <stdint.h>
#include <iosfwd>

// Based on http://www-mmsp.ece.mcgill.ca/Documents/AudioFormats/WAVE/WAVE.html
// and http://www.dragonwins.com/domains/getteched/wav/

struct RiffChunk
{
    static const int sizeBytes = 8;
    
    uint32_t id;
    // Payload size (bytes)
    uint32_t size;
};

struct FmtExtensionFields
{
    static const int sizeBytes = 22;
    
    uint16_t wValidBitsPerSample;
    uint32_t dwChannelMask;
    uint8_t GUID[16];
};

// PCM format
struct FmtChunk : public RiffChunk
{
    // Does not include optional fields
    static const int sizeBytes = RiffChunk::sizeBytes + 16;
    
    static const uint32_t ID = 0x20746d66; // "fmt "
    static const uint16_t WAVE_FORMAT_PCM = 0x0001;
    static const uint16_t WAVE_FORMAT_IEEE_FLOAT = 0x0003;
    static const uint16_t WAVE_FORMAT_ALAW = 0x0006;
    static const uint16_t WAVE_FORMAT_MULAW = 0x0007;
    static const uint16_t WAVE_FORMAT_EXTENSIBLE = 0xFFFE;
    
    // size = 16, 18 or 40 for 3 standard Formats
    
    // PCM = 1 (i.e. Linear quantization)
    uint16_t wFormatTag;
    
    uint16_t nChannels;
    // Hz
    uint32_t nSamplesPerSec;

    // SampleRate * NumChannels * BitsPerSample/8
    uint32_t nAvgBytesPerSec;

    // NumChannels * BitsPerSample/8
    uint16_t nBlockAlign;
    uint16_t wBitsPerSample;
    
    // Optional
    uint16_t cbSize; // 0 or 22
    FmtExtensionFields extension;
};

struct FactChunk : public RiffChunk
{
    static const int sizeBytes = RiffChunk::sizeBytes + 4;
    
    static const uint32_t ID = 0x74636166; // "fact"
    //FactChunk();
    uint32_t dwSampleLength;
};

struct DataChunk : public RiffChunk
{
    static const int sizeBytes = RiffChunk::sizeBytes;
    static const uint32_t ID = 0x61746164; // "data"
};

struct WaveHeader : public RiffChunk
{
    static const uint32_t ID = 0x46464952; // RIFF
    static const uint32_t WAVEID = 0x45564157; // "wave"
    uint32_t waveId;
    FmtChunk fmt;
    FactChunk fact; // Optional
    DataChunk data;
};

bool hasFmtExtensionSizeField(const WaveHeader& wav);
bool hasFmtExtensionFields(const WaveHeader& wav);
bool hasFactChunk(const WaveHeader& wav);

size_t dataOffsetBytes(const WaveHeader& wav);

void readWaveHeader(std::istream& in, WaveHeader& wav);
void readWaveHeader(const char* filename, WaveHeader& wav);

#endif	/* WAVE_FILE_H */

