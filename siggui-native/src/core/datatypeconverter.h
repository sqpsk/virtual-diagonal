#ifndef DATATYPECONVERTER_H
#define DATATYPECONVERTER_H

#include "byteswap.h"
#include <limits>

enum data_type {
invalid_data_type = -1, twos_t = 0, offset_t = 1, ieee_float_t = 2
};

class IDataTypeConverter 
{
public:
	virtual size_t inSampleSizeBytes() = 0;

	// out should be inSizeBytes / inSampleSizeBytes()
	virtual void work(const char* in, size_t inSizeBytes, float* out) = 0;
};
// TODO SSE versions of these
template <typename T, T OffSet, bool byteSwap>
class IntegerConverter : public IDataTypeConverter {
public:
	IntegerConverter() : _scale(-1.0f / std::numeric_limits<T>::min()) { }

	size_t inSampleSizeBytes() { return sizeof (T); }

	float convert0(T t) 
	{
		T twosValue = t + OffSet;
		return twosValue * _scale;
	}

	float convert(T t) 
	{
		if (byteSwap)
			return convert0(byte_swap(t));
		else
			return convert0(t);
	}

	void work(const T* in, size_t inSizeSamples, float* out) 
	{
		for (size_t i = 0; i != inSizeSamples; ++i)
			out[i] = convert(in[i]);
	}

	void work(const char* in, size_t inSizeSamples, float* out) 
	{
		work(reinterpret_cast<const T*> (in), inSizeSamples, out);
	}

private:
	const float _scale;
};

template <typename T, bool byteSwap>
class FloatConverter : public IDataTypeConverter {
public:

	size_t inSampleSizeBytes() { return sizeof (T); }

	float convert(T t)
	{
		if (byteSwap)
			return static_cast<float>(byte_swap(t));
		else
			return static_cast<float>(t);
	}

	void work(const T* in, size_t inSizeSamples, float* out)
	{
		for (size_t i = 0; i != inSizeSamples; ++i) 
			out[i] = convert(in[i]);
	}

	void work(const char* in, size_t inSizeSamples, float* out)
	{
		work(reinterpret_cast<const T*> (in), inSizeSamples, out);
	}
};

IDataTypeConverter* makeDataTypeConverter(data_type data_t, int bits, bool littleEndian);

#endif /* DATATYPECONVERTER_H */