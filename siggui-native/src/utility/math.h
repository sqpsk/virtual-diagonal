#ifndef UTILITY_MATH_H
#define UTILITY_MATH_H

#include <cassert>
#include <complex>
#include <cmath>
#include <cstdlib> // for abs(int)
#include <utility>

#include <boost/math/constants/constants.hpp>
#include <boost/math/special_functions/round.hpp>
#include <boost/version.hpp>

#if BOOST_VERSION < 104600
namespace boost { namespace math {namespace constants {
BOOST_DEFINE_MATH_CONSTANT(two_pi, 3.141592653589793238462643383279502884197169399375105820974944, 59230781640628620899862803482534211706798214808651328230664709384460955058223172535940812848111745028410270193852110555964462294895493038196, 0)
} } }
#endif

template <typename T> 
inline T minValue()
{
	if (std::numeric_limits<T>::is_integer) 
		return std::numeric_limits<T>::min(); 
	else 
		return -std::numeric_limits<T>::max();
}

template <typename T>
inline T pi()
{
	return boost::math::constants::pi<T>();
}

template <typename T>
inline T two_pi()
{
	return boost::math::constants::two_pi<T>();
}

template <typename T>
inline T pi_2()
{
	return boost::math::constants::pi<T>() / 2;
}

template <typename T>
inline T pi_4()
{
	return boost::math::constants::pi<T>() / T(4);
}

template <typename T>
inline T pi_8()
{
	return boost::math::constants::pi<T>() / T(8);
}

template <typename T>
inline T two_pi_inv()
{
	// boost one_div_two_pi is wrong!
	//return boost::math::constants::one_div_two_pi<T>();
	return static_cast<T>(0.5 * M_1_PI);
}

template <typename T>
inline T deg_to_rad()
{
	return pi<T>() / T(180);
}

template <typename T>
inline T rad_to_deg()
{
	return T(180) / pi<T>();
}

template <typename T>
inline void setReal(std::complex<T>& z, const T& x)
{
#ifdef _MSC_VER
	z.real(x);
#else
	z.real() = x;
#endif
}

template <typename T>
inline void setImag(std::complex<T>& z, const T& x)
{
#ifdef _MSC_VER
	z.imag(x);
#else
	z.imag() = x;
#endif
}

template <typename T>
inline T bound(T low, T x, T high)
{
	return std::min(std::max(low, x), high);
}

// 10.0f * std::log10(FLT_MIN); //-379.298 (fp:precise)
// 10.0f * std::log10(FLT_MIN); //-758.596 (fp:fast)
template <typename T> inline
T min_power_db()
{
	return T(-140);
}

template <typename T> inline
T powerDecibels(const std::complex<T>& z)
{
	return T(10) * std::log10(std::norm(z));
}

template <typename T> inline
T powerDecibelsSafe(const std::complex<T>& z)
{
	return std::max(min_power_db<T>(), powerDecibels(z));
}

inline
float powerDecibels(float z)
{
	return 10.0f*std::log10(z * z);
}

inline
float powerDecibelsSafe(float z)
{
	return std::max(min_power_db<float>(), powerDecibels(z));
}

inline int log2(int x)
{
	assert(x > 0);
	int l = 0;
	while (x > 0)
	{
		++l;
		x <<= 1;
	}
	return l;
}

#endif /* UTILITY_MATH_H */
