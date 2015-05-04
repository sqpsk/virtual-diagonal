#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran
AS=as

# Macros
CND_PLATFORM=GNU-Linux-x86
CND_DLIB_EXT=so
CND_CONF=Release
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/_ext/1019403175/datatypeconverter.o \
	${OBJECTDIR}/_ext/1019403175/fileinfo.o \
	${OBJECTDIR}/_ext/1019403175/filesource.o \
	${OBJECTDIR}/_ext/1019403175/sigguinative.o \
	${OBJECTDIR}/_ext/659859371/jniwrapper.o \
	${OBJECTDIR}/_ext/1266421357/SampleFormat.o \
	${OBJECTDIR}/_ext/1266421357/WaveFileProperties.o \
	${OBJECTDIR}/_ext/58301666/fft.o \
	${OBJECTDIR}/_ext/58301666/fftwindows.o \
	${OBJECTDIR}/_ext/2044043147/spectrumjni.o \
	${OBJECTDIR}/_ext/2044043147/spectrumsink.o \
	${OBJECTDIR}/_ext/406928194/timeseriesjni.o \
	${OBJECTDIR}/_ext/406928194/timeseriessink.o \
	${OBJECTDIR}/_ext/1018820685/wavefile.o \
	${OBJECTDIR}/_ext/1018820685/wavefunctions.o


# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=-m64
CXXFLAGS=-m64

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=-L/usr/lib64

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libsiggui-native.${CND_DLIB_EXT}

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libsiggui-native.${CND_DLIB_EXT}: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libsiggui-native.${CND_DLIB_EXT} ${OBJECTFILES} ${LDLIBSOPTIONS} -lfftw3 -lfftw3f -shared -fPIC

${OBJECTDIR}/_ext/1019403175/datatypeconverter.o: ../src/core/datatypeconverter.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1019403175
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1019403175/datatypeconverter.o ../src/core/datatypeconverter.cpp

${OBJECTDIR}/_ext/1019403175/fileinfo.o: ../src/core/fileinfo.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1019403175
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1019403175/fileinfo.o ../src/core/fileinfo.cpp

${OBJECTDIR}/_ext/1019403175/filesource.o: ../src/core/filesource.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1019403175
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1019403175/filesource.o ../src/core/filesource.cpp

${OBJECTDIR}/_ext/1019403175/sigguinative.o: ../src/core/sigguinative.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1019403175
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1019403175/sigguinative.o ../src/core/sigguinative.cpp

${OBJECTDIR}/_ext/659859371/jniwrapper.o: ../src/jni/jniwrapper.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/659859371
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/659859371/jniwrapper.o ../src/jni/jniwrapper.cpp

${OBJECTDIR}/_ext/1266421357/SampleFormat.o: ../src/properties/SampleFormat.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1266421357
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1266421357/SampleFormat.o ../src/properties/SampleFormat.cpp

${OBJECTDIR}/_ext/1266421357/WaveFileProperties.o: ../src/properties/WaveFileProperties.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1266421357
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1266421357/WaveFileProperties.o ../src/properties/WaveFileProperties.cpp

${OBJECTDIR}/_ext/58301666/fft.o: ../src/signal/fft.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/58301666
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/58301666/fft.o ../src/signal/fft.cpp

${OBJECTDIR}/_ext/58301666/fftwindows.o: ../src/signal/fftwindows.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/58301666
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/58301666/fftwindows.o ../src/signal/fftwindows.cpp

${OBJECTDIR}/_ext/2044043147/spectrumjni.o: ../src/spectrum/spectrumjni.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/2044043147
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/2044043147/spectrumjni.o ../src/spectrum/spectrumjni.cpp

${OBJECTDIR}/_ext/2044043147/spectrumsink.o: ../src/spectrum/spectrumsink.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/2044043147
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/2044043147/spectrumsink.o ../src/spectrum/spectrumsink.cpp

${OBJECTDIR}/_ext/406928194/timeseriesjni.o: ../src/timeseries/timeseriesjni.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/406928194
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/406928194/timeseriesjni.o ../src/timeseries/timeseriesjni.cpp

${OBJECTDIR}/_ext/406928194/timeseriessink.o: ../src/timeseries/timeseriessink.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/406928194
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/406928194/timeseriessink.o ../src/timeseries/timeseriessink.cpp

${OBJECTDIR}/_ext/1018820685/wavefile.o: ../src/wave/wavefile.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1018820685
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1018820685/wavefile.o ../src/wave/wavefile.cpp

${OBJECTDIR}/_ext/1018820685/wavefunctions.o: ../src/wave/wavefunctions.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1018820685
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Wall -DBOOST_SYSTEM_NO_DEPRECATED -I/usr/include/boost -I/usr/include -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include/linux -I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.1.el7_1.x86_64/include -I../src -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1018820685/wavefunctions.o ../src/wave/wavefunctions.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libsiggui-native.${CND_DLIB_EXT}

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
