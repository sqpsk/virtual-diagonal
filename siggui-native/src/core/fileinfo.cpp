#include "fileinfo.h"
#include <iostream>

FileHolder FileHolder::INSTANCE;

FileInfo::FileInfo() :
	filename(),
	data_t(invalid_data_type),
	isComplex(false),
	bytesPerSample(0),
	isLittleEndian(false),
	headerSizeBytes(0),
	sampleRateHz(0),
	converter()
{
}

const char* toString(data_type data_t)
{
	switch (data_t)
	{
	case twos_t:
		return "twos";
	case offset_t:
		return "offset";
	case ieee_float_t:
		return "float";
	default:
		return "???";
	}
}

std::ostream& operator<<(std::ostream& out, const FileInfo& info)
{
	out << info.filename;
	out << ' ' << (info.isComplex ? "complex" : "real");
	out << ' ' << toString(info.data_t) << (info.bytesPerSample * 8);
	out << ' ' << (info.isLittleEndian ? "little" : "big") << "-endian";
	return out;
}
