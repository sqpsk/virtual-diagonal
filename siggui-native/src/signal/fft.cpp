#include "fft.h"

#include <cassert>
#include <iostream>

template <unsigned int n>
struct CountSetBits
{
	static const unsigned int value = CountSetBits< (n >> 1)>::value + (n & 1);
};

template <>
struct CountSetBits<0>
{
	static const unsigned int value = 0;
};

int log2(int size)
{
	assert(size > 0);
	int l = 0;
	while (size != 1)
	{
		++l;
		size >>= 1;
	}
	return l;
}

// FFTW planning functions are not thread safe.

void
createPlans(int logMinFftSize, int logMaxFftSize, fftwf_complex* inPtr, fftwf_complex* outPtr, int dir, fftwf_plan*& plans)
{
	for (int i = logMinFftSize; i <= logMaxFftSize; ++i)
	{
		fftwf_plan p = fftwf_plan_dft_1d(1 << i, inPtr, outPtr, dir, FFTW_ESTIMATE);
		*(plans++) = p;
	}
}

void
createR2CPlans(int logMinFftSize, int logMaxFftSize, float* inPtr, fftwf_complex* outPtr, fftwf_plan*& plans)
{
	for (int i = logMinFftSize; i <= logMaxFftSize; ++i)
	{
		fftwf_plan p = fftwf_plan_dft_r2c_1d(1 << i, inPtr, outPtr, FFTW_ESTIMATE);
		*(plans++) = p;
	}
}

void
createC2RPlans(int logMinFftSize, int logMaxFftSize, fftwf_complex* inPtr, float* outPtr, fftwf_plan*& plans)
{
	for (int i = logMinFftSize; i <= logMaxFftSize; ++i)
	{
		fftwf_plan p = fftwf_plan_dft_c2r_1d(1 << i, inPtr, outPtr, FFTW_ESTIMATE);
		*(plans++) = p;
	}
}

void
destroyPlans(fftwf_plan* plans, int planCount)
{
	for (int i = 0; i != planCount; ++i)
		fftwf_destroy_plan(plans[i]);
}

// [fft_inplace, fft_outplace,ifft_inplace, ifft_outplace,
//  re_fft_inplace, re_fft_outplace,re_ifft_inplace, re_ifft_outplace]
// Masks
#define FFT_INPLACE 1
#define FFT_OUTPLACE 2
#define IFFT_INPLACE 4
#define IFFT_OUTPLACE 8

#define REAL_FFT_INPLACE 16
#define REAL_FFT_OUTPLACE 32
#define REAL_IFFT_INPLACE 64
#define REAL_IFFT_OUTPLACE 128

template <int LogMinFftSize, int LogMaxFftSize, int PlanTypeMask>
class PlanCache
{
public:
	PlanCache()
	{
		if (fftwf_import_wisdom_from_filename("fftw.wisdom") != 1)
		{
			std::cout << "FFTW wisdom file not found. Planning..." << std::endl;
		}

		fftw_buf_cf in(1 << LogMaxFftSize);
		fftwf_complex* inPtr = reinterpret_cast<fftwf_complex*> (&in[0]);

		fftw_buf_cf out;
		fftwf_complex* outPtr = NULL;

		if (PlanTypeMask & (FFT_OUTPLACE | IFFT_OUTPLACE | REAL_FFT_OUTPLACE | REAL_IFFT_OUTPLACE))
		{
			out.resize(in.size());
			outPtr = reinterpret_cast<fftwf_complex*> (&out[0]);
		}

		fftwf_plan* plans = _plan;
		if (PlanTypeMask & FFT_INPLACE)
			createPlans(LogMinFftSize, LogMaxFftSize, inPtr, inPtr, FFTW_FORWARD, plans);

		if (PlanTypeMask & FFT_OUTPLACE)
			createPlans(LogMinFftSize, LogMaxFftSize, inPtr, outPtr, FFTW_BACKWARD, plans);

		if (PlanTypeMask & IFFT_INPLACE)
			createPlans(LogMinFftSize, LogMaxFftSize, inPtr, inPtr, FFTW_BACKWARD, plans);

		if (PlanTypeMask & IFFT_OUTPLACE)
			createPlans(LogMinFftSize, LogMaxFftSize, inPtr, outPtr, FFTW_BACKWARD, plans);

		if (PlanTypeMask & REAL_FFT_INPLACE)
			createR2CPlans(LogMinFftSize, LogMaxFftSize, reinterpret_cast<float*> (inPtr), inPtr, plans);

		if (PlanTypeMask & REAL_FFT_OUTPLACE)
			createR2CPlans(LogMinFftSize, LogMaxFftSize, reinterpret_cast<float*> (inPtr), outPtr, plans);

		if (PlanTypeMask & REAL_IFFT_INPLACE)
			createC2RPlans(LogMinFftSize, LogMaxFftSize, inPtr, reinterpret_cast<float*> (inPtr), plans);

		if (PlanTypeMask & REAL_IFFT_OUTPLACE)
			createC2RPlans(LogMinFftSize, LogMaxFftSize, inPtr, reinterpret_cast<float*> (outPtr), plans);

		if (fftwf_export_wisdom_to_filename("fftw.wisdom") != 1)
			std::cout << "Failed to export FFTW wisdom" << std::endl;
	}

	~PlanCache()
	{
		destroyPlans(_plan, (1 + LogMaxFftSize - LogMinFftSize) * CountSetBits<PlanTypeMask>::value);
	}

	inline fftwf_plan
	get_fft_inplace_plan(int size) const
	{
		return get_plan<FFT_INPLACE>(size);
	}

	inline fftwf_plan
	get_fft_outplace_plan(int size) const
	{
		return get_plan<FFT_OUTPLACE>(size);
	}

	inline fftwf_plan
	get_ifft_inplace_plan(int size) const
	{
		return get_plan<IFFT_INPLACE>(size);
	}

	inline fftwf_plan
	get_ifft_outplace_plan(int size) const
	{
		return get_plan<IFFT_OUTPLACE>(size);
	}

	inline fftwf_plan
	get_fft_real_inplace_plan(int size) const
	{
		return get_plan<REAL_FFT_INPLACE>(size);
	}

	inline fftwf_plan
	get_fft_real_outplace_plan(int size) const
	{
		return get_plan<REAL_FFT_OUTPLACE>(size);
	}

	inline fftwf_plan
	get_ifft_real_inplace_plan(int size) const
	{
		return get_plan<REAL_IFFT_INPLACE>(size);
	}

	inline fftwf_plan
	get_ifft_real_outplace_plan(int size) const
	{
		return get_plan<REAL_IFFT_OUTPLACE>(size);
	}

private:

	template <int TypeMask> inline
	fftwf_plan
	get_plan(int size) const
	{
		assert((1 << LogMinFftSize) <= size && size <= (1 << LogMaxFftSize));
		return _plan[(CountSetBits < PlanTypeMask & (TypeMask - 1)>::value) * (1 + LogMaxFftSize - LogMinFftSize) + log2(size) - LogMinFftSize];
	}

    fftwf_plan _plan[CountSetBits<PlanTypeMask>::value * (1 + LogMaxFftSize - LogMinFftSize)];
};

// SigGui plans FFT size 128 to 1M
static PlanCache < 1, 20, FFT_INPLACE | REAL_FFT_INPLACE> PLAN_CACHE;

void
fft(const std::complex<float>* in, std::complex<float>* out, int size)
{
	assert(in != out);
	fftwf_execute_dft(
			PLAN_CACHE.get_fft_outplace_plan(size),
			reinterpret_cast<fftwf_complex*> (const_cast<std::complex<float>*> (in)),
			reinterpret_cast<fftwf_complex*> (out));
}

void
fft(std::complex<float>* inout, int size)
{
	fftwf_execute_dft(
		PLAN_CACHE.get_fft_inplace_plan(size),
		reinterpret_cast<fftwf_complex*> (inout),
		reinterpret_cast<fftwf_complex*> (inout));
}

void
ifft(const std::complex<float>* in, std::complex<float>* out, int size)
{
	assert(in != out);
	fftwf_execute_dft(
		PLAN_CACHE.get_ifft_outplace_plan(size),
		reinterpret_cast<fftwf_complex*> (const_cast<std::complex<float>*> (in)),
		reinterpret_cast<fftwf_complex*> (out));
}

void
ifft(std::complex<float>* inout, int size)
{
	fftwf_execute_dft(
		PLAN_CACHE.get_ifft_inplace_plan(size),
		reinterpret_cast<fftwf_complex*> (inout),
		reinterpret_cast<fftwf_complex*> (inout));
}

// The non cached functions

void
fft_once(const std::complex<float>* in, std::complex<float>* out, int size)
{
	fftwf_plan plan = fftwf_plan_dft_1d(
		size,
		reinterpret_cast<fftwf_complex*> (const_cast<std::complex<float>*> (in)),
		reinterpret_cast<fftwf_complex*> (out),
		FFTW_FORWARD,
		FFTW_ESTIMATE);
	fftwf_execute(plan);
	fftwf_destroy_plan(plan);
}

void
ifft_once(const std::complex<float>* in, std::complex<float>* out, int size)
{
	fftwf_plan plan = fftwf_plan_dft_1d(
		size,
		reinterpret_cast<fftwf_complex*> (const_cast<std::complex<float>*> (in)),
		reinterpret_cast<fftwf_complex*> (out),
		FFTW_BACKWARD,
		FFTW_ESTIMATE);
	fftwf_execute(plan);
	fftwf_destroy_plan(plan);
}

//
// REAL r2c is forward, c2r is backward
//

void fft(const float* in, std::complex<float>* out, int size)
{
	assert(in != reinterpret_cast<const float*>(out));
	 fftwf_execute_dft_r2c(
		PLAN_CACHE.get_fft_real_outplace_plan(size),
		const_cast<float*> (in),
		reinterpret_cast<fftwf_complex*> (out));
}

void fft(void* inout, int size)
{
	fftwf_execute_dft_r2c(
		PLAN_CACHE.get_fft_real_inplace_plan(size),
		reinterpret_cast<float*> (inout),
		reinterpret_cast<fftwf_complex*> (inout));
}

void ifft(const std::complex<float>* in, float* out, int size)
{
	assert(reinterpret_cast<float*> (const_cast<std::complex<float>*> (in)) != reinterpret_cast<float*>(out));
	fftwf_execute_dft_c2r(
		PLAN_CACHE.get_ifft_real_outplace_plan(size),
		reinterpret_cast<fftwf_complex*> (const_cast<std::complex<float>*> (in)),
		reinterpret_cast<float*> (out));
}

void ifft(void* inout, int size)
{
	fftwf_execute_dft_c2r(
		PLAN_CACHE.get_ifft_real_inplace_plan(size),
		reinterpret_cast<fftwf_complex*> (inout),
		reinterpret_cast<float*> (inout));
}

void
fft_once(const float* in, std::complex<float>* out, int size)
{
	// in should contain size samples
	// out should contain (size/2) + 1 samples
	fftwf_plan plan = fftwf_plan_dft_r2c_1d(
		size,
		const_cast<float*> (in),
		reinterpret_cast<fftwf_complex*> (out),
		FFTW_ESTIMATE);
	fftwf_execute(plan);
	fftwf_destroy_plan(plan);
}

void
ifft_once(const std::complex<float>* in, float* out, int size)
{
	fftwf_plan plan = fftwf_plan_dft_c2r_1d(
		size,
		reinterpret_cast<fftwf_complex*> (const_cast<std::complex<float>*> (in)),
		out,
		FFTW_ESTIMATE);
	fftwf_execute(plan);
	fftwf_destroy_plan(plan);
}
