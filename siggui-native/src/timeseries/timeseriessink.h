#ifndef TIMESERIES_SINK_H
#define TIMESERIES_SINK_H

#include <complex>
#include "utility/math.h"

class FileSource;
class IDataTypeConverter;

struct SimpleBlock
{
	virtual int decimation() = 0;
	virtual void work(const float* in, size_t inSize, float* out) = 0;
};

template <int Offset, int Step>
struct CopyBlock : public SimpleBlock
{
	int decimation() { return Step; }

	void work(const float* in, size_t inSize, float* out)
	{
		for (int i = 0; i != inSize / Step; ++i)
			out[i] = in[Offset + i * Step];
	}
};

template <>
struct CopyBlock<0, 1> : public SimpleBlock
{
	int decimation() { return 1; }

	void work(const float* in, size_t inSize, float* out)
	{
	std::copy(in, in + inSize, out);
	}
};

template <typename T>
struct PowerBlock : public SimpleBlock
{
	int decimation() { return sizeof (T) / sizeof (float); }

	void work(const float* in, size_t inSize, float* out)
	{
	workImpl(reinterpret_cast<const T*>(in), inSize / decimation(), out);
	}

	void workImpl(const T* in, size_t inSize, float* out)
	{
	for (int i = 0; i != inSize; ++i)
		out[i] = powerDecibelsSafe(in[i]);
	}
};

struct PhaseBlock : public SimpleBlock
{
	int decimation() { return 2; }

	void work(const float* in, size_t inSize, float* out)
	{
		workImpl(reinterpret_cast<const std::complex<float>*>(in), inSize / 2, out);
	}

	void workImpl(const std::complex<float>* in, size_t inSize, float* out)
	{
		for (size_t i = 0; i != inSize; ++i)
			out[i] = std::arg(in[i]);
	}
};

SimpleBlock* makeBlock(int type, bool isComplex);

void run(FileSource& src, size_t inSizeFloats, IDataTypeConverter& converter, SimpleBlock& fun, float* out);

void run(FileSource& src, size_t inSizeFloats, IDataTypeConverter& converter, float* re, float* im);

#endif /* TIMESERIES_SINK_H */