set VERSION=1.0.0
set NATIVE_LIB_PATH="..\siggui-native\vs2010\Release\siggui-native.dll"
call mvn clean install -DskipTests -f ../zplot/pom.xml
call mvn clean install -DskipTests
mkdir target\siggui-%VERSION%
mkdir target\siggui-%VERSION%\lib
copy %NATIVE_LIB_PATH% target\siggui-%VERSION%\lib\siggui-native.dll
copy "C:\Program Files\fftw\libfftw3-3.dll" target\siggui-%VERSION%\lib\libfftw3-3.dll
copy "C:\Program Files\fftw\libfftw3f-3.dll" target\siggui-%VERSION%\lib\libfftw3f-3.dll
copy target\siggui-%VERSION%-jar-with-dependencies.jar target\siggui-%VERSION%\siggui-%VERSION%-jar-with-dependencies.jar
copy siggui.bat target\siggui-%VERSION%\siggui.bat
cd target
C:\"Program Files (x86)"\IZArc\izarcc -a -P -r .\siggui-%VERSION%-windows.zip siggui-%VERSION%
cd ..