#ifndef FILE_INFO_H
#define FILE_INFO_H

#include <string>
#include <boost/shared_ptr.hpp>
// Use this instead of <boost/thread/mutex.hpp> to avoid linking against boost_thread
#include <boost/detail/lightweight_mutex.hpp>
#include "datatypeconverter.h"

struct FileInfo
{
	FileInfo();

	IDataTypeConverter& getConverter() { return *converter.get(); }
	size_t bytesPerSample2() { return isComplex ? 2 * bytesPerSample : bytesPerSample; }
	size_t byteOffset(size_t sample) { return headerSizeBytes + (bytesPerSample2() * sample); }

	std::string filename;
	data_type data_t;
	bool isComplex;
	int bytesPerSample;
	bool isLittleEndian;
	size_t headerSizeBytes;
	size_t sampleRateHz;
	std::auto_ptr<IDataTypeConverter> converter;
};

std::ostream& operator<<(std::ostream& out, const FileInfo& info);

class FileHolder
{
public:
	static void reset(const boost::shared_ptr<FileInfo>& info)
	{
		INSTANCE._reset(info);
	}

	static boost::shared_ptr<FileInfo> get()
	{
		return INSTANCE._get();
	}

private:
	void _reset(const boost::shared_ptr<FileInfo>& info)
	{
		boost::detail::lightweight_mutex::scoped_lock lock(_mutex);
		_info = info; 
	}

	boost::shared_ptr<FileInfo> _get() const 
	{
		boost::detail::lightweight_mutex::scoped_lock lock(_mutex);
		return _info; 
	}

	static FileHolder INSTANCE;
	mutable boost::detail::lightweight_mutex _mutex;
	boost::shared_ptr<FileInfo> _info;
};

#endif /* FILE_INFO_H */