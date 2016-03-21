#include <cassert>
#include <fstream>
#include <iostream>
#include <boost/make_shared.hpp>

#include "javah/siggui_properties_SampleFormat.h"
#include "javah/siggui_SigGuiNative.h"
#include "javah/siggui_SigGuiController.h"
#include "properties/SampleFormat.h"
#include "properties/WaveFileProperties.h"
#include "wave/wavefile.h"
#include "wave/wavefunctions.h"
#include "fileinfo.h"

void timeseriesInit(JNIEnv* env);
void timeseriesDestroy(JNIEnv* env);

void spectrumInit(JNIEnv* env);
void spectrumDestroy(JNIEnv* env);

void makeTestFiles();

size_t getFileSize(std::ifstream& ifs)
{
	ifs.seekg(0, std::ios::end);
	size_t size = static_cast<size_t>(ifs.tellg());
	ifs.seekg(std::ios::beg);
	return size;
}

JNIEXPORT void JNICALL Java_siggui_SigGuiNative_initialize
 (JNIEnv* env, jclass cls)
{
	SampleFormatClass::init(env);
	WaveFilePropertiesClass::init(env);
	timeseriesInit(env);
	spectrumInit(env);
}

JNIEXPORT void JNICALL Java_siggui_SigGuiNative_destroy
 (JNIEnv* env, jclass cls)
{
	timeseriesDestroy(env);
	spectrumDestroy(env);
}

JNIEXPORT jobject JNICALL Java_siggui_SigGuiController_readWavHeader
	(JNIEnv* env, jclass, jstring jfilename)
{
	const char* filename = env->GetStringUTFChars(jfilename, NULL);
	std::ifstream ifs(filename, std::ios::binary);
	env->ReleaseStringUTFChars(jfilename, filename);

	WaveHeader wf;
	readWaveHeader(ifs, wf);

	if (wf.id != WaveHeader::ID || wf.waveId != WaveHeader::WAVEID)
	{
		// Not a wav file
		return NULL;
	}

	if (wf.fmt.wFormatTag != 1)
	{
		std::cout << "Cannot read compressed wav file: Compression type " << wf.fmt.wFormatTag << std::endl;
		return NULL;
	}

	printWaveHeader(wf, std::cout);
	std::cout << std::endl;

	int channelCount = wf.fmt.nChannels;
	int sampleSizeBytes = (wf.fmt.nBlockAlign / channelCount);
	size_t headerSizeBytes = dataOffsetBytes(wf);
	size_t fileSize = getFileSize(ifs);
	size_t sampleCount = (fileSize - headerSizeBytes) / (channelCount * sampleSizeBytes);

	jint jSampleType;
	if (wf.fmt.wFormatTag == FmtChunk::WAVE_FORMAT_EXTENSIBLE)
	{
		jSampleType = siggui_properties_SampleFormat_SAMPLE_TYPE_FLOAT;
	}
	else if (sampleSizeBytes == 1)
	{
		jSampleType = siggui_properties_SampleFormat_SAMPLE_TYPE_OFFSET;
	}
	else
	{
		jSampleType = siggui_properties_SampleFormat_SAMPLE_TYPE_TWOS;
	}

	jobject jSampleFormat =
		SampleFormatClass::instance().newInstance(env, wf.fmt.nChannels == 2 ? JNI_TRUE : JNI_FALSE,
		jSampleType,
		8 * sampleSizeBytes,
		JNI_TRUE);

	return WaveFilePropertiesClass::instance().newObject(env, jSampleFormat, headerSizeBytes, sampleCount, wf.fmt.nSamplesPerSec);
}

JNIEXPORT void JNICALL Java_siggui_SigGuiController_setFile(
	JNIEnv* env, 
	jclass, 
	jstring filename, 
	jboolean isComplex, 
	jint sampleType, 
	jint bitsPerSample, 
	jboolean isLittleEndian, 
	jlong headerSizeBytes, 
	jlong sampleRateHz)
{
	boost::shared_ptr<FileInfo> info = boost::make_shared<FileInfo>();

	const char* filenamePtr = env->GetStringUTFChars(filename, NULL);
	info->filename = filenamePtr;
	env->ReleaseStringUTFChars(filename, filenamePtr);

	switch (sampleType)
	{
	case siggui_properties_SampleFormat_SAMPLE_TYPE_TWOS:
		info->data_t = twos_t;
		break;
	case siggui_properties_SampleFormat_SAMPLE_TYPE_OFFSET:
		info->data_t = offset_t;
		break;
	case siggui_properties_SampleFormat_SAMPLE_TYPE_FLOAT:
		info->data_t = ieee_float_t;
		break;
	default:
		info->data_t = twos_t;
		std::cout << "Error: Unknown sample type " << sampleType << std::endl;
		break;
	}

	info->isComplex = isComplex == JNI_TRUE;
	info->bytesPerSample = bitsPerSample / 8;
	info->isLittleEndian = isLittleEndian == JNI_TRUE;
	info->headerSizeBytes = static_cast<size_t>(headerSizeBytes);
	info->sampleRateHz = static_cast<size_t>(sampleRateHz);
	info->converter.reset(makeDataTypeConverter(info->data_t, info->bytesPerSample * 8, info->isLittleEndian));
	FileHolder::reset(info);
}
