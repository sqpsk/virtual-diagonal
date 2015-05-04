#ifndef WAVE_FUNCTIONS_H
#define WAVE_FUNCTIONS_H

#include <stdint.h>
#include <iosfwd>

struct RiffChunk;
struct WaveHeader;

void printRiffChunkHeader(const RiffChunk& c, const char* prefix, std::ostream& out);
void printGuid(const uint8_t guid[16], std::ostream& out);
void printWaveHeader(const WaveHeader& wav, std::ostream& out);

#endif /* WAVE_FUNCTIONS_H */