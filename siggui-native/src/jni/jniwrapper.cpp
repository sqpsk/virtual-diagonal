#include "jniwrapper.h"

#define JNI_TRAITS_SPECIALISATION(jtype, signature, Name) \
template<> jni_traits<jtype>::get_t jni_traits<jtype>::get_member = &JNIEnv::Get##Name##Field; \
template<> jni_traits<jtype>::set_t jni_traits<jtype>::set_member = &JNIEnv::Set##Name##Field; \
template<> const char* builtin_jni_traits<jtype>::type_signature = signature; \

JNI_TRAITS_SPECIALISATION(jbyte, "B", Byte)
JNI_TRAITS_SPECIALISATION(jchar, "C", Char)
JNI_TRAITS_SPECIALISATION(jdouble, "D", Double)
JNI_TRAITS_SPECIALISATION(jfloat, "F", Float)
JNI_TRAITS_SPECIALISATION(jint, "I", Int)
JNI_TRAITS_SPECIALISATION(jlong, "J", Long)
JNI_TRAITS_SPECIALISATION(jshort, "S", Short)
JNI_TRAITS_SPECIALISATION(jboolean, "Z", Boolean)
template<> jni_traits<jobject>::get_t jni_traits<jobject>::get_member = &JNIEnv::GetObjectField;
template<> jni_traits<jobject>::set_t jni_traits<jobject>::set_member = &JNIEnv::SetObjectField;

JavaClass::JavaClass(JNIEnv* env, const char* signature) :
	_env(env),
	_cls(env->FindClass(signature))
{
	assert(_cls != NULL);
}

jobject JavaClass::newInstance()
{
	jmethodID constructor = _env->GetMethodID(_cls, "<init>", "()V");
	assert(constructor != NULL);
	return _env->NewObject(_cls, constructor);
}
