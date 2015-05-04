#ifndef BYTE_SWAP_H
#define BYTE_SWAP_H

#include <stddef.h>
#include <stdint.h>

namespace detail {

template <typename T, size_t size>
struct byte_swap {};

template <typename T>
struct byte_swap<T, 1>
{
	static inline T swap(T val) { return val; }
};

template<typename T>
struct byte_swap<T, 2>
{
	static inline T swap(T val)
	{
		return ((val >> 8) & 0xff) | ((val & 0xff) << 8);
	}
};

template<typename T>
struct byte_swap<T, 4>
{
	static inline T swap(T val)
	{
#if defined(_USE_BUILTIN_BSWAPS) && defined(__GNUC__) && ((__GNUC__ == 4 && __GNUC_MINOR__ >= 3) || __GNUC__ > 4)
		return __builtin_bswap32(val);
#else
		return ((val & 0xff000000) >> 24) |
				((val & 0x00ff0000) >>  8) |
				((val & 0x0000ff00) <<  8) |
				((val & 0x000000ff) << 24);
#endif
	}
};

template<typename T>
struct byte_swap<T, 8>
{
	static inline T swap(T val)
	{
#if defined(_USE_BUILTIN_BSWAPS) && defined(__GNUC__) && ((__GNUC__ == 4 && __GNUC_MINOR__ >= 3) || __GNUC__ > 4)
		return __builtin_bswap64(val);
#else
		return ((val & 0xff00000000000000ull) >> 56) |
			((val & 0x00ff000000000000ull) >> 40) |
			((val & 0x0000ff0000000000ull) >> 24) |
			((val & 0x000000ff00000000ull) >> 8 ) |
			((val & 0x00000000ff000000ull) << 8 ) |
			((val & 0x0000000000ff0000ull) << 24) |
			((val & 0x000000000000ff00ull) << 40) |
			((val & 0x00000000000000ffull) << 56);
#endif
	}
};

// float and double specialisations with casts.
template <>
struct byte_swap <float, 4>
{
	static inline float swap(float val)
	{
		uint32_t mem = *reinterpret_cast<uint32_t*>(&val);
		mem = byte_swap<uint32_t, 4>::swap(mem);
		return *reinterpret_cast<float*>(&mem);
	}
};

template <>
struct byte_swap <double, 8>
{
	static inline double swap(double val)
	{
		uint64_t mem = *reinterpret_cast<uint64_t*>(&val);
		mem = byte_swap<uint64_t, 8>::swap(mem);
		return *reinterpret_cast<double*>(&mem);
	}
};

} // end namespace detail

template <typename T> inline 
T byte_swap(T val) 
{
	return detail::byte_swap<T, sizeof (T)>::swap(val);
}

#endif /* BYTE_SWAP_H */