set VERSION=1.0.0
rem call mvn clean install -DskipTests -f ../zplot/pom.xml
rem call mvn clean install -DskipTests

REM Package x64 build
set ARCH=x64
set NATIVE_LIB="..\siggui-native\vs2015\x64\Release\siggui-native.dll"
set FFTW_PATH="C:\Program Files\fftw-3.3.4-dll64"
mkdir target\%ARCH%
mkdir target\%ARCH%\siggui-%VERSION%
mkdir target\%ARCH%\siggui-%VERSION%\lib
copy %NATIVE_LIB% target\%ARCH%\siggui-%VERSION%\lib\siggui-native.dll
copy %FFTW_PATH%\libfftw3-3.dll target\%ARCH%\siggui-%VERSION%\lib\libfftw3-3.dll
copy %FFTW_PATH%\libfftw3f-3.dll target\%ARCH%\siggui-%VERSION%\lib\libfftw3f-3.dll
copy target\siggui-%VERSION%-jar-with-dependencies.jar target\%ARCH%\siggui-%VERSION%\siggui-%VERSION%-jar-with-dependencies.jar
copy siggui.bat target\%ARCH%\siggui-%VERSION%\siggui.bat
cd target\%ARCH%
C:\"Program Files (x86)"\IZArc\izarcc -a -P -r .\siggui-%VERSION%-windows-%ARCH%.zip siggui-%VERSION%
cd ..\..

REM Package win32 build
set ARCH=win32
set NATIVE_LIB="..\siggui-native\vs2015\Release\siggui-native.dll"
set FFTW_PATH="C:\Program Files\fftw-3.3.4-dll32"
mkdir target\%ARCH%
mkdir target\%ARCH%\siggui-%VERSION%
mkdir target\%ARCH%\siggui-%VERSION%\lib
copy %NATIVE_LIB% target\%ARCH%\siggui-%VERSION%\lib\siggui-native.dll
copy %FFTW_PATH%\libfftw3-3.dll target\%ARCH%\siggui-%VERSION%\lib\libfftw3-3.dll
copy %FFTW_PATH%\libfftw3f-3.dll target\%ARCH%\siggui-%VERSION%\lib\libfftw3f-3.dll
copy target\siggui-%VERSION%-jar-with-dependencies.jar target\%ARCH%\siggui-%VERSION%\siggui-%VERSION%-jar-with-dependencies.jar
copy siggui.bat target\%ARCH%\siggui-%VERSION%\siggui.bat
cd target\%ARCH%
C:\"Program Files (x86)"\IZArc\izarcc -a -P -r .\siggui-%VERSION%-windows-%ARCH%.zip siggui-%VERSION%
cd ..\..