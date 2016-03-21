#include "javah/siggui_spectrum_SpectrumParameters.h"
#include "javah/siggui_spectrum_SpectrumTask.h"
#include "jni/jniwrapper.h"
#include "core/fileinfo.h"
#include "core/filesource.h"
#include "spectrumsink.h"
#include "signal/fftwindows.h"
#include <iostream>

struct SpectrumResultClass : public JavaClassBase 
{
	static void init(JNIEnv* env) 
	{
		INSTANCE.initImpl(env);
	}

	static void destroy(JNIEnv* env) 
	{
		INSTANCE.destroyImpl(env);
	}

	static const SpectrumResultClass & instance() 
	{
		return INSTANCE;
	}

	void setFields(JNIEnv* env, jobject obj, const SpectrumResult& r) const;
	jmethodID constructor;
	jfieldID power;

private:
	static SpectrumResultClass INSTANCE;
	SpectrumResultClass();
	void initImpl(JNIEnv* env);

	void destroyImpl(JNIEnv* env) 
	{
		JavaClassBase::destroy(env);
	}
};

SpectrumResultClass SpectrumResultClass::INSTANCE;

SpectrumResultClass::SpectrumResultClass() :
	JavaClassBase("siggui/spectrum/SpectrumResult"),
	constructor(NULL),
	power(NULL) 
{
}

void SpectrumResultClass::initImpl(JNIEnv* env) 
{
	JavaClassBase::init(env);
	constructor = getDefaultConstructor(env, cls);
	assert(constructor != NULL);
	power = env->GetFieldID(cls, "power", "[F");
	assert(power != NULL);
}

void SpectrumResultClass::setFields(JNIEnv* env, jobject obj, const SpectrumResult& r) const 
{
	setArrayMember(env, obj, power, reinterpret_cast<const jfloat*> (&r.powerDb[0]), static_cast<jsize>(r.powerDb.size()));
}

void spectrumInit(JNIEnv* env) 
{
	SpectrumResultClass::init(env);
}

void spectrumDestroy(JNIEnv* env) 
{
	SpectrumResultClass::destroy(env);
}

void printArgs(jlong sampleBegin, jlong sampleEnd, jint fftSize, jint fftStep, jint fftCount, jint window) 
{
	std::cout << "SpectrumTask_calculateNative: region=[" << sampleBegin << ", " << sampleEnd << ")";
	std::cout << " fftSize=" << fftSize;
	std::cout << " fftStep=" << fftStep;
	std::cout << " fftCount=" << fftCount;
	std::cout << " window=" << window;
	std::cout << std::endl;
}

JNIEXPORT jobject JNICALL Java_siggui_spectrum_SpectrumTask_calculateNative(
	JNIEnv* env,
	jclass cls,
	jlong sampleBegin,
	jlong sampleEnd,
	jint fftSize,
	jint fftStep,
	jint fftCount,
	jint window) {

	boost::shared_ptr<FileInfo> info = FileHolder::get();
	FileSource src(info->filename.c_str(), info->byteOffset(static_cast<size_t>(sampleBegin)), info->byteOffset(static_cast<size_t>(sampleEnd)));

	std::vector<float> windowFunction(fftSize);
	switch (window)
	{
	default:
		fillGaussWindow(&windowFunction[0], static_cast<int>(windowFunction.size()), 0.2);
		break;
	case siggui_spectrum_SpectrumParameters_WINDOW_TRIANGLE:
		fillTriangularWindow(&windowFunction[0], static_cast<int>(windowFunction.size()));
		break;
	case siggui_spectrum_SpectrumParameters_WINDOW_RECTANGLE:
		std::fill(&windowFunction[0], &windowFunction[0] + windowFunction.size(), 1.0f);
		break;
	}

	std::auto_ptr<ISpectrumTask> task;
	if (info->isComplex) 
	{
		task.reset(new SpectrumTask<std::complex<float> >(fftSize, fftStep, &windowFunction[0]));
	}
	else
	{
		task.reset(new SpectrumTask<float>(fftSize, fftStep, &windowFunction[0]));
	}

	task->run(src, info->getConverter(), fftCount);

	jobject result = env->NewObject(SpectrumResultClass::instance().cls, SpectrumResultClass::instance().constructor);

	SpectrumResultClass::instance().setFields(env, result, task->result());

	return result;
}
