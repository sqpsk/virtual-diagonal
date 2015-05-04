#include "SampleFormat.h"
#include "javah/siggui_properties_SampleFormat.h"

SampleFormatClass SampleFormatClass::INSTANCE;

void SampleFormatClass::init(JNIEnv* env)
{
	INSTANCE.initImpl(env);
}

SampleFormatClass::SampleFormatClass() : 
	JavaClassBase("siggui/properties/SampleFormat"),
	newInstanceID(NULL),
	constructorID(NULL),
	isComplexID(NULL),
	sampleTypeID(NULL),
	bitsPerSampleID(NULL),
	isLittleEndianID(NULL)
{}

void SampleFormatClass::initImpl(JNIEnv* env)
{
	JavaClassBase::init(env);

	newInstanceID = env->GetStaticMethodID(cls, "make", "(ZIIZ)Lsiggui/properties/SampleFormat;");
	assert(newInstanceID != NULL);

	constructorID = env->GetMethodID(cls, "<init>", "(ZIIZ)V");
	assert(constructorID != NULL);

	isComplexID = env->GetFieldID(cls, "isComplex", builtin_jni_traits<jboolean>::type_signature);
	assert(isComplexID != NULL);

	sampleTypeID = env->GetFieldID(cls, "sampleType", builtin_jni_traits<jint>::type_signature);
	assert(sampleTypeID != NULL);

	bitsPerSampleID = env->GetFieldID(cls, "bitsPerSample", builtin_jni_traits<jint>::type_signature);
	assert(bitsPerSampleID != NULL);

	isLittleEndianID = env->GetFieldID(cls, "isLittleEndian", builtin_jni_traits<jboolean>::type_signature);
	assert(isLittleEndianID != NULL);
}
