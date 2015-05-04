#include "WaveFileProperties.h"
#include "javah/siggui_properties_WaveFileProperties.h"

WaveFilePropertiesClass WaveFilePropertiesClass::INSTANCE;

void WaveFilePropertiesClass::init(JNIEnv* env)
{
	INSTANCE.initImpl(env);
}

WaveFilePropertiesClass::WaveFilePropertiesClass() :
	JavaClassBase("siggui/properties/WaveFileProperties"),
	constructor(NULL)
{}

void WaveFilePropertiesClass::initImpl(JNIEnv* env)
{
	JavaClassBase::init(env);
	constructor = env->GetMethodID(cls, "<init>", "(Lsiggui/properties/SampleFormat;JJJ)V");
	assert(constructor != NULL);
}
