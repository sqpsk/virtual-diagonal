#include "datatypeconverter.h"
#include <stdint.h>
#include <cassert>
#include <iostream>
#include <boost/integer_traits.hpp>
#include <boost/detail/endian.hpp>

bool is_little_endian()
{
	union
	{
		uint32_t i;
		char c[4];
	} u;
	u.i = 1;
	return u.c[0] == 1;
}

template <typename T, T Offset>
IDataTypeConverter* makeIntegerConverter(bool littleEndian)
{
#ifdef BOOST_LITTLE_ENDIAN
	if (littleEndian)
		return new IntegerConverter<T, Offset, false>();
	else
		return new IntegerConverter<T, Offset, true>();
#else
	if (littleEndian)
		return new IntegerConverter<T, Offset, true>();
	else
		return new IntegerConverter<T, Offset, false>();
#endif
}

template <typename T>
IDataTypeConverter* makeFloatConverter(bool littleEndian)
{
#ifdef BOOST_LITTLE_ENDIAN
	if (littleEndian)
		return new FloatConverter<T, false>();
	else
		return new FloatConverter<T, true>();
#else
	if (littleEndian)
		return new FloatConverter<T, true>();
	else
		return new FloatConverter<T, false>();
#endif
}

IDataTypeConverter* makeDataTypeConverter(data_type data_t, int bits, bool littleEndian)
{
	switch (data_t)
	{
	case twos_t:
		switch (bits)
		{
		case 8:
			return makeIntegerConverter<int8_t, 0>(littleEndian);
		case 16:
			return makeIntegerConverter<int16_t, 0>(littleEndian);
		case 32:
			return makeIntegerConverter<int32_t, 0>(littleEndian);
		case 64:
			return makeIntegerConverter<int64_t, 0>(littleEndian);
		default:
			std::cout << __FUNCTION__ << ": Error: Unsupported size " << bits << std::endl;
			assert(false);
			return NULL;
		}
	case offset_t:
		switch (bits)
		{
		case 8:
			return makeIntegerConverter<int8_t, boost::integer_traits<int8_t>::const_min>(littleEndian);
		case 16:
			return makeIntegerConverter<int16_t, boost::integer_traits<int16_t>::const_min>(littleEndian);
		case 32:
			return makeIntegerConverter<int32_t, boost::integer_traits<int32_t>::const_min>(littleEndian);
		case 64:
			return makeIntegerConverter<int64_t, boost::integer_traits<int64_t>::const_min>(littleEndian);
		default:
			std::cout << __FUNCTION__ << ": Error: Unsupported size " << bits << std::endl;
			assert(false);
			return NULL;
		}
	case ieee_float_t:
		switch (bits)
		{
		case 32:
			return makeFloatConverter<float>(littleEndian);
		case 64:
			return makeFloatConverter<double>(littleEndian);
		default:
			std::cout << __FUNCTION__ << ": Error: Unsupported size " << bits << std::endl;
			assert(false);
			return NULL;
		}
	default:
		std::cout << __FUNCTION__ << ": Error: Unknown data type " << data_t << std::endl;
		assert(false);
		return NULL;
	}
}
