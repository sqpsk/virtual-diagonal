#include "spectrumsink.h"
#include <algorithm>
#include "core/filesource.h"
#include "core/datatypeconverter.h"
#include "utility/math.h"

int coverSize(int blockSize, int blockStep, int blockCount)
{
	return blockSize + (blockCount - 1) * blockStep;
}

template <class InType>
SpectrumTask<InType>::SpectrumTask(int fftSize, int fftStep, const float* window) :
	_window(window),
	_fftSize(fftSize),
	_fftStep(fftStep),
	_work(sizeof (InType) == sizeof (float) ? 2*((_fftSize / 2) + 1) : _fftSize),
	_r(sizeof (InType) == sizeof (float) ? (_fftSize / 2) + 1 : _fftSize)
{
}

template <class InType>
void SpectrumTask<InType>::run(FileSource& src, IDataTypeConverter& converter, int fftCount)
{
	const int fftsPerBatch = std::min(fftCount, 20);
	const int isComplex = sizeof (InType) == sizeof (float) ? 1 : 2;
	const int batchCount = fftCount / fftsPerBatch;
	const int fftOverlap = _fftSize - _fftStep;

	// Raw file bytes buffer
	std::vector<char, fftw_allocator<char> > buff0(fftsPerBatch * _fftStep * isComplex * converter.inSampleSizeBytes());

	// Input (complex) float buffer
	std::vector<InType, fftw_allocator<InType> > buff1(coverSize(_fftSize, _fftStep, fftsPerBatch));

	// Walm up buffers
	src.work(&buff0[0], fftOverlap * isComplex * converter.inSampleSizeBytes());

	converter.work(&buff0[0], fftOverlap * isComplex, reinterpret_cast<float*> (&buff1[0]));

	for (int i = 0; i != batchCount; ++i)
	{
		src.work(&buff0[0], buff0.size());

		// Number of input samples
		converter.work(&buff0[0], fftsPerBatch * _fftStep * isComplex, reinterpret_cast<float*> (&buff1[fftOverlap]));
		work(&buff1[0], fftsPerBatch);

		std::copy(buff1.end() - fftOverlap, buff1.end(), buff1.begin());
	}

	const int remainderFftCount = fftCount - (batchCount * fftsPerBatch);
	if (remainderFftCount > 0)
	{
		const int remainderSamples = remainderFftCount * _fftStep;
		// Cool down
		src.work(&buff0[0], remainderSamples * isComplex * converter.inSampleSizeBytes());
		converter.work(&buff0[0], remainderSamples * isComplex, reinterpret_cast<float*> (&buff1[fftOverlap]));
		work(&buff1[0], remainderFftCount);
	}
	float scale = 1.0f / fftCount;
	for (int j = 0; j != _r.powerDb.size(); ++j)
		_r.powerDb[j] = powerDecibelsSafe(_r.powerDb[j] * scale);

	if (sizeof (InType) == sizeof (std::complex<float>))
		std::rotate(&_r.powerDb[0], &_r.powerDb[0] + _fftSize / 2, &_r.powerDb[0] + _fftSize);
}

template <class InType>
void SpectrumTask<InType>::work(const InType* in, int fftCount)
{
	for (int i = 0; i != fftCount; ++i)
	{
		for (int j = 0; j != _fftSize; ++j)
			_work[j] = _window[j] * in[j];

		fft(reinterpret_cast<InType*>(&_work[0]), _fftSize);

		const std::complex<float>* out = reinterpret_cast<std::complex<float>*>(&_work[0]);
		for (size_t j = 0; j != _r.powerDb.size(); ++j)
			_r.powerDb[j] += std::norm(out[j]);

		in += _fftStep;
	}
}

template class SpectrumTask<float>;
template class SpectrumTask<std::complex<float> >;
