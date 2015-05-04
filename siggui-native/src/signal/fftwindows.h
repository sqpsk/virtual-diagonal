#ifndef FFT_WINDOWS_H
#define	FFT_WINDOWS_H

#include <cmath>

// All windows have the symmetry w(i) = w(N-1-i) (i = 0,...,N-1)

template <typename T>
void reflect(T* out, int size) 
{
	const int htap = size / 2;
	for (int i = 0; i != htap; ++i) 
		out[size - 1 - i] = out[i];
}

template <typename T>
void fillTriangularWindow(T* out, int size) 
{
	const int nvalues = (size + 1) / 2;
	for (int i = 0; i != nvalues; ++i) 
		out[i] = static_cast<T> (2.0 * i / size);
	reflect(out, size);
}

#define M_1_SQRT2PI	0.3989422804143

template <typename T>
void fillGaussWindow(T* out, int size, double sigma) 
{
	const int nvalues = (size + 1) / 2;
	const int htap = size / 2;
	double scale = M_1_SQRT2PI / sigma;

	for (int i = 0; i != nvalues; ++i) 
	{
		double y = ((i - htap) + 0.5) / (htap * sigma);
		double arg = -0.5 * y * y;
		out[i] = static_cast<T> (scale * std::exp(arg));
	}
	reflect(out, size);
}

#endif	/* FFT_WINDOWS_H */
