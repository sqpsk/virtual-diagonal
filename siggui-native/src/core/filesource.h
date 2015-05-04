#ifndef FILE_SOURCE_H
#define FILE_SOURCE_H

#include <fstream>

class FileSource
{
public:
	FileSource(const char* filename, size_t beginByte, size_t endByte);
	~FileSource();
	bool done() const;
	size_t work(char* data, size_t maxOutSizeBytes);

private:
	mutable std::ifstream _ifs;
	size_t _maxOutSizeBytes;
};

#endif /* FILE_SOURCE_H */

