#ifndef SPECTRUM_SINK_H
#define SPECTRUM_SINK_H

#include <vector>
#include "signal/fft.h"

class FileSource;
class IDataTypeConverter;

struct SpectrumResult 
{
	SpectrumResult(size_t size) : powerDb(size, 0.0f) {}

	std::vector<float> powerDb;
};

struct ISpectrumTask
{
	virtual ~ISpectrumTask(){};
	virtual void run(FileSource& src, IDataTypeConverter& converter, int fftCount) = 0;
	virtual const SpectrumResult& result() const = 0;
};

template <class InType>
class SpectrumTask : public ISpectrumTask {
public:
	SpectrumTask(int fftSize, int fftStep, const float* window);

	void run(FileSource& src, IDataTypeConverter& converter, int fftCount);
	void work(const InType* in, int fftCount);

	const SpectrumResult& result() const { return _r; }
private:
	const float* const _window;
	const int _fftSize;
	const int _fftStep;
	FFT_VEC(InType) _work;
	SpectrumResult _r;
};

#endif /* SPECTRUM_SINK_H */