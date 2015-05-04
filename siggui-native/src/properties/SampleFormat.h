#ifndef SAMPLE_FORMAT_H
#define SAMPLE_FORMAT_H

#include "jni/jniwrapper.h"

class SampleFormatClass : public JavaClassBase
{
public:
	static const SampleFormatClass& instance() { return INSTANCE; }
	static void init(JNIEnv* env);

	jobject newInstance(JNIEnv* env, jboolean isComplex, jint sampleType, jint bitsPerSample, jboolean isLittleEndian) const
	{
		return env->CallStaticObjectMethod(cls, newInstanceID, isComplex, sampleType, bitsPerSample, isLittleEndian);
	}

private:
	// TODO we can probably get rid of all this stuff
	static jboolean isComplex(JNIEnv* env, jobject obj)
	{
		return (env->*jni_traits<jboolean>::get_member)(obj, INSTANCE.isComplexID);
	}

	static jint sampleType(JNIEnv* env, jobject obj)
	{
		return (env->*jni_traits<jint>::get_member)(obj, INSTANCE.sampleTypeID);
	}

	static jint bitsPerSample(JNIEnv* env, jobject obj)
	{
		return (env->*jni_traits<jint>::get_member)(obj, INSTANCE.bitsPerSampleID);
	}

	static jboolean isLittleEndian(JNIEnv* env, jobject obj)
	{
		return (env->*jni_traits<jboolean>::get_member)(obj, INSTANCE.isLittleEndianID);
	}
	
	jobject newObject(JNIEnv* env, jboolean isComplex, jint sampleType, jint bitsPerSample, jboolean isLittleEndian) const
	{
		return env->NewObject(cls, constructorID, isComplex, sampleType, bitsPerSample, isLittleEndian);
	}

	SampleFormatClass();
	void initImpl(JNIEnv* env);
	static SampleFormatClass INSTANCE;
	jmethodID newInstanceID;
	jmethodID constructorID;
	jfieldID isComplexID;
	jfieldID sampleTypeID;
	jfieldID bitsPerSampleID;
	jfieldID isLittleEndianID;
};

#endif /* SAMPLE_FORMAT_H */
