#ifndef SIGNALPROCESSING_FFT_H
#define SIGNALPROCESSING_FFT_H

#include <complex>
#include <vector>
#include <fftw3.h>

template<typename T>
class fftw_allocator {
public:
	typedef size_t size_type;
	typedef ptrdiff_t difference_type;
	typedef T* pointer;
	typedef const T* const_pointer;
	typedef T& reference;
	typedef const T& const_reference;
	typedef T value_type;

	fftw_allocator() {};

	fftw_allocator(const fftw_allocator&) {};

	template<typename U>
	fftw_allocator(const fftw_allocator<U>&) {}

	template<typename U>
	struct rebind
	{
		typedef fftw_allocator<U> other;
	};

	pointer address(reference x) const
	{
		return &x;
	}

	const_pointer address(const_reference x) const
	{
		return &x;
	}

	// NB: n is permitted to be 0. The C++ standard says nothing
	// about what the return value is when n == 0.

	pointer allocate(size_type n, const void* = 0)
	{
		if (n > this->max_size())
			throw std::bad_alloc();
		return static_cast<T*> (fftw_malloc(n * sizeof (T)));
	}

	// p is not permitted to be a null pointer.
	void deallocate(pointer p, size_type)
	{
		fftw_free(p);
	}

	size_type max_size() const
	{
		return size_type(-1) / sizeof (T);
	}

	void construct(pointer p, const T& v)
	{
		::new((void *) p) T(v);
	}

	void destroy(pointer p)
	{
		p->~T();
	}
};

template<typename T>
inline bool operator==(const fftw_allocator<T>&, const fftw_allocator<T>&)
{
	return true;
}

template<typename T>
inline bool operator!=(const fftw_allocator<T>&, const fftw_allocator<T>&)
{
	return false;
}

#define FFT_VEC(T) std::vector<T, fftw_allocator<T> >
typedef FFT_VEC(float) fftw_buf;
typedef FFT_VEC(std::complex<float>) fftw_buf_cf;

void fft(const std::complex<float>* in, std::complex<float>* out, int size);
void fft(std::complex<float>* inout, int size);
void ifft(const std::complex<float>* in, std::complex<float>* out, int size);
void ifft(std::complex<float>* inout, int size);

// The input is n real numbers, while the output is n/2+1 complex numbers.
void fft(const float* in, std::complex<float>* out, int size);

// inout should contain 2*((size/2) + 1) float samples
void fft(void* inout, int size);

// The input is n real numbers, while the output is n/2+1 complex numbers
void ifft(const std::complex<float>* in, float* out, int size);

// inout should contain 2*((size/2) + 1) samples

// The buffer inout must contain 2*(n/2+1) floats.
void ifft(void* inout, int size);

// The non cached functions - No special inplace functions
void fft_once(const std::complex<float>* in, std::complex<float>* out, int size);
void ifft_once(const std::complex<float>* in, std::complex<float>* out, int size);
void fft_once(const float* in, std::complex<float>* out, int size);
void ifft_once(const std::complex<float>* in, float* out, int size);

#endif /* SIGNALPROCESSING_FFT_H */