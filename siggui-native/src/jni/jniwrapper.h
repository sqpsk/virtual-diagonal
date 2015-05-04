#ifndef JNI_WRAPPER_H
#define JNI_WRAPPER_H

#include <cassert>
#include <jni.h>

template <typename T>
struct jni_traits
{
	typedef T(JNIEnv::*get_t)(jobject, jfieldID);
	typedef void (JNIEnv::*set_t)(jobject, jfieldID, T);
	typedef T jtype;
	static get_t get_member;
	static set_t set_member;
	static const char* type_signature;
};

template <typename T>
struct builtin_jni_traits : public jni_traits<T>
{
	static const char* type_signature;
};

#define CLASSTYPE_JNI_TRAITS_SPECIALISATION(ClassName, signature) \
struct ClassName##_traits : public jni_traits<jobject> { static const char* type_signature; }; \
const char* ClassName##_traits::type_signature = signature;

inline
jmethodID getDefaultConstructor(JNIEnv* env, jclass cls)
{
	return env->GetMethodID(cls, "<init>", "()V");
}

template <typename jtype> inline
void setMember(JNIEnv* env, jobject obj, jfieldID id, jtype value)
{
	(env->*jni_traits<jtype>::set_member)(obj, id, value);
}

template <typename jtype> inline
void setArrayMember(JNIEnv* env, jobject obj, jfieldID id, const jtype* values, size_t size)
{
	jfloatArray arr = env->NewFloatArray(size);
	// TODO traits stuff
	env->SetFloatArrayRegion(arr, 0, size, const_cast<jtype*>(values));
	env->SetObjectField(obj, id, arr);
}

// DO NOT cache jclasses or jobjects across different JNI invocations without using a GlobalReference. 
// You CAN cache methodIDs and fieldIDs.
// See http://stackoverflow.com/questions/10617735/in-jni-how-do-i-cache-the-class-methodid-and-fieldids-per-ibms-performance-r

struct JavaClassBase
{
	JavaClassBase(const char* sig) : type_signature(sig), cls(NULL) {}

	void init(JNIEnv* env)
	{
		jclass tempLocalClassRef = env->FindClass(type_signature);
		assert(tempLocalClassRef != NULL);

		cls = static_cast<jclass> (env->NewGlobalRef(tempLocalClassRef));
		assert(cls != NULL);

		env->DeleteLocalRef(tempLocalClassRef);
	}

	void destroy(JNIEnv* env)
	{
		env->DeleteGlobalRef(cls);
	}
	const char* type_signature;
	jclass cls;
};

class JavaClass
{
public:
	JavaClass(JNIEnv* env, const char* signature);

	jclass cls()
	{
		return _cls;
	}
	jobject newInstance();

	template <typename jtype>
	void setMember(jobject obj, jfieldID id, jtype value)
	{
		(_env->*jni_traits<jtype>::set_member)(obj, id, value);
	}

	template <typename jtype>
	void setObjectFieldOnce(const char* fieldName, jobject obj, jtype value)
	{
		setObjectFieldOnce<jtype>(fieldName, obj, builtin_jni_traits<jtype>::type_signature, value);
	}

	template <typename jtype>
	void setObjectFieldOnce(const char* fieldName, jobject obj, const char* signature, jtype value)
	{
		jfieldID id = _env->GetFieldID(_cls, fieldName, signature);
		assert(id != NULL);
		(_env->*jni_traits<jtype>::set_member)(obj, id, value);
	}

protected:
	JNIEnv* _env;
	const jclass _cls;
};

#endif /* JNI_WRAPPER_H */
