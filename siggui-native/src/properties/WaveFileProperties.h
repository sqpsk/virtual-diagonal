#ifndef WAVE_FILE_PROPERTIES_H
#define WAVE_FILE_PROPERTIES_H

#include "jni/jniwrapper.h"

class WaveFilePropertiesClass : public JavaClassBase
{
public:
	static const WaveFilePropertiesClass& instance() { return INSTANCE; }
	static void init(JNIEnv* env);

	jobject newObject(
		JNIEnv* env, 
		jobject sampleFormat, 
		jlong headerSizeBytes,
		jlong sampleCount,
		jlong sampleRateHz) const
	{
		return env->NewObject(cls, constructor, sampleFormat, headerSizeBytes, sampleCount, sampleRateHz);
	}

private:
	WaveFilePropertiesClass();
	void initImpl(JNIEnv* env);
	static WaveFilePropertiesClass INSTANCE;
	jmethodID constructor;
};

#endif /* WAVE_FILE_PROPERTIES_H */
