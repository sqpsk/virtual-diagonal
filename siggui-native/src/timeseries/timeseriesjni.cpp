#include "javah/siggui_perspectives_timeseries_TimeseriesTask.h"
#include "javah/siggui_perspectives_timeseries_TimeseriesParameters.h"

#include <cassert>
#include <iostream>

#include "jni/jniwrapper.h"
#include "core/fileinfo.h"
#include "core/filesource.h"
#include "timeseriessink.h"

struct TimeseriesResultClass : public JavaClassBase
{
	static void init(JNIEnv* env) { INSTANCE.initImpl(env); }
	static void destroy(JNIEnv* env) { INSTANCE.destroyImpl(env); }
	static const TimeseriesResultClass& instance() { return INSTANCE; }

	void setFields(JNIEnv* env, jobject obj, jint keyValue, const std::vector<float>& buf, jlong beginValue, jlong endValue) const;
	void setAdditionalFields(JNIEnv* env, jobject obj, jint keyValue, const std::vector<float>& buf) const;

	jmethodID constructor;
	jfieldID begin;
	jfieldID end;
	jfieldID data;
	jfieldID key;
	jfieldID data1;
	jfieldID key1;

private:
	static TimeseriesResultClass INSTANCE;
	TimeseriesResultClass();
	void initImpl(JNIEnv* env);
	void destroyImpl(JNIEnv* env) { JavaClassBase::destroy(env); }
};

TimeseriesResultClass TimeseriesResultClass::INSTANCE;

TimeseriesResultClass::TimeseriesResultClass() : 
	JavaClassBase("siggui/perspectives/timeseries/TwoSeriesTimeseriesResult"),
	constructor(NULL),
	begin(NULL),
	end(NULL),
	data(NULL),
	key(NULL),
	data1(NULL),
	key1(NULL)
{}

void TimeseriesResultClass::initImpl(JNIEnv* env)
{
	JavaClassBase::init(env);
	constructor = getDefaultConstructor(env, cls);
	assert(constructor != NULL);
	begin = env->GetFieldID(cls, "begin", builtin_jni_traits<jlong>::type_signature);
	assert(begin != NULL);
	end = env->GetFieldID(cls, "end", builtin_jni_traits<jlong>::type_signature);
	assert(end != NULL);
	data = env->GetFieldID(cls, "data", "[F");
	assert(data != NULL);
	key = env->GetFieldID(cls, "key", "I");
	assert(key != NULL);
	data1 = env->GetFieldID(cls, "data1", "[F");
	assert(data1 != NULL);
	key1 = env->GetFieldID(cls, "key1", "I");
	assert(key1 != NULL);
}

void TimeseriesResultClass::setFields(
	JNIEnv* env, jobject obj, jint keyValue, const std::vector<float>& buf, jlong beginValue, jlong endValue) const
{
	setArrayMember(env, obj, data, &buf[0], static_cast<jsize>(buf.size()));
	setMember(env, obj, key, keyValue);
	setMember(env, obj, begin, beginValue);
	setMember(env, obj, end, endValue);
}

void TimeseriesResultClass::setAdditionalFields(JNIEnv* env, jobject obj, jint keyValue, const std::vector<float>& buf) const
{
	setArrayMember(env, obj, data1, &buf[0], static_cast<jsize>(buf.size()));
	setMember(env, obj, key1, keyValue);
}

void timeseriesInit(JNIEnv* env)
{
	TimeseriesResultClass::init(env);
}

void timeseriesDestroy(JNIEnv* env)
{
	TimeseriesResultClass::destroy(env);
}

SimpleBlock* makeBlock(int seriesType, bool isComplex)
{
	if (isComplex)
	{
		switch (seriesType)
		{
		default:
			return new PowerBlock<std::complex<float> >();
		case siggui_perspectives_timeseries_TimeseriesParameters_SERIES_I:
			return new CopyBlock<0, 2>();
		case siggui_perspectives_timeseries_TimeseriesParameters_SERIES_Q:
			return new CopyBlock<1, 2>();
		case siggui_perspectives_timeseries_TimeseriesParameters_SERIES_IvQ:
			return new CopyBlock<0, 1>();
		case siggui_perspectives_timeseries_TimeseriesParameters_SERIES_PHASE:
			return new PhaseBlock();
		}
	}
	else // real
	{
		if (seriesType == siggui_perspectives_timeseries_TimeseriesParameters_SERIES_I)
			return new CopyBlock<0, 1>();
		else
			return new PowerBlock<float>();
	}
	assert(false);
	return NULL;
}

JNIEXPORT jobject JNICALL Java_siggui_timeseries_TimeseriesTask_calculateNative
(JNIEnv* env, jclass cls, jlong sampleBegin, jlong sampleEnd, jint seriesType)
{
	//std::cout << "TimeseriesTask_calculateNative: [" << sampleBegin << ", " <<  sampleEnd << ") type=" << seriesType << std::endl;
	boost::shared_ptr<FileInfo> info = FileHolder::get();
	FileSource src(
		info->filename.c_str(), 
		info->byteOffset(static_cast<size_t>(sampleBegin)), 
		info->byteOffset(static_cast<size_t>(sampleEnd)));
	const size_t samples = static_cast<size_t>(sampleEnd - sampleBegin);

	if (seriesType != siggui_perspectives_timeseries_TimeseriesParameters_SERIES_I_AND_Q)
	{
		std::auto_ptr<SimpleBlock> block(makeBlock(seriesType, info->isComplex));

		const size_t inSizeFloats = info->isComplex ? 2 * samples : samples;

		std::vector<float> out(inSizeFloats / block->decimation());

		run(src, inSizeFloats, info->getConverter(), *block, &out[0]);

		jobject result = env->NewObject(
			TimeseriesResultClass::instance().cls, 
			TimeseriesResultClass::instance().constructor);
		assert(result != NULL);
		TimeseriesResultClass::instance().setFields(env, result, seriesType, out, sampleBegin, sampleEnd);
		return result;
	}
	else // I and Q
	{
		assert(info->isComplex);
		std::vector<float> re(samples);
		std::vector<float> im(samples);

		run(src, samples * 2, info->getConverter(), &re[0], &im[0]);

		jobject result = env->NewObject(
			TimeseriesResultClass::instance().cls, 
			TimeseriesResultClass::instance().constructor);
		assert(result != NULL);
		TimeseriesResultClass::instance().setFields(env, result, siggui_perspectives_timeseries_TimeseriesParameters_SERIES_I, re, sampleBegin, sampleEnd);
		TimeseriesResultClass::instance().setAdditionalFields(env, result, siggui_perspectives_timeseries_TimeseriesParameters_SERIES_Q, im);
		return result;
	}
}
