#include "filesource.h"
#include <iomanip>
#include <cassert>

FileSource::FileSource(const char* filename, size_t beginByte, size_t endByte) :
	_ifs(filename, std::ios::binary),
	_maxOutSizeBytes(endByte - beginByte)
{
	_ifs.seekg(beginByte, std::ios::beg);
}

FileSource::~FileSource()
{
	assert(_maxOutSizeBytes == 0);
	assert(_maxOutSizeBytes == 0);
}

bool FileSource::done() const
{
	return _maxOutSizeBytes == 0 || !_ifs.good();
}

size_t FileSource::work(char* out, size_t maxOutSizeBytes)
{
	assert(_maxOutSizeBytes > 0);
	const size_t requestBytes = std::min(_maxOutSizeBytes, maxOutSizeBytes);
	_ifs.read(out, requestBytes);
	const size_t outSizeBytes = static_cast<size_t>(_ifs.gcount());
	assert(outSizeBytes == requestBytes);
	_maxOutSizeBytes -= outSizeBytes;
	return outSizeBytes;
}
