#include "timeseriessink.h"

#include <vector>
#include "core/filesource.h"
#include "core/datatypeconverter.h"

void run(FileSource& src, size_t inSizeFloats, IDataTypeConverter& converter, SimpleBlock& fun, float* out)
{
	static const int CONVERT_DECIMATION = 1;

	// Block sizes in floats
	const size_t SRC_OUT_SIZE = std::min(inSizeFloats, size_t(4096));
	const size_t CONVERT_OUT_SIZE = SRC_OUT_SIZE / CONVERT_DECIMATION;
	const size_t FUNCTOR_OUT_SIZE = CONVERT_OUT_SIZE / fun.decimation();

	const size_t batchCount = (inSizeFloats + SRC_OUT_SIZE - 1) / SRC_OUT_SIZE;
	std::vector<char> buff0(SRC_OUT_SIZE * converter.inSampleSizeBytes());
	std::vector<float> buff1(CONVERT_OUT_SIZE);

	for (size_t i = 0; i != batchCount; ++i)
	{
		const size_t srcOutSize = std::min(SRC_OUT_SIZE, inSizeFloats - i * SRC_OUT_SIZE);
		const size_t convertOutSize = srcOutSize / CONVERT_DECIMATION;

		// We can easily parallelise this because the output size is a known function
		// of the input size and the blocks are indepentent
		float* const outPtr = out + i * FUNCTOR_OUT_SIZE;

		src.work(&buff0[0], srcOutSize * converter.inSampleSizeBytes());
		converter.work(&buff0[0], srcOutSize, &buff1[0]);
		fun.work(&buff1[0], convertOutSize, outPtr);
	}
}

void run(FileSource& src, size_t inSizeFloats, IDataTypeConverter& converter, float* re, float* im)
{
	static const int CONVERT_DECIMATION = 1;
	static const int FUNCTOR_DECIMATION = 2;

	// All sizes in floats
	const size_t SRC_OUT_SIZE = std::min(inSizeFloats, size_t(4096));
	const size_t CONVERT_OUT_SIZE = SRC_OUT_SIZE / CONVERT_DECIMATION;
	const size_t FUNCTOR_OUT_SIZE = CONVERT_OUT_SIZE / FUNCTOR_DECIMATION;

	const size_t batchCount = (inSizeFloats + SRC_OUT_SIZE - 1) / SRC_OUT_SIZE;
	std::vector<char> buff0(SRC_OUT_SIZE * converter.inSampleSizeBytes());
	std::vector<float> buff1(CONVERT_OUT_SIZE);

	for (size_t i = 0; i != batchCount; ++i)
	{
		const size_t srcOutSize = std::min(SRC_OUT_SIZE, inSizeFloats - i * SRC_OUT_SIZE);
		const size_t convertOutSize = srcOutSize / CONVERT_DECIMATION;

		float* const reOutPtr = re + i * FUNCTOR_OUT_SIZE;
		float* const imOutPtr = im + i * FUNCTOR_OUT_SIZE;

		src.work(&buff0[0], srcOutSize * converter.inSampleSizeBytes());
		converter.work(&buff0[0], srcOutSize, &buff1[0]);
		for (size_t j = 0; j != convertOutSize/2; ++j)
		{
			reOutPtr[j] = buff1[2*j];
			imOutPtr[j] = buff1[2*j + 1];
		}
	}
}
