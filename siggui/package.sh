VERSION=1.0.0
NATIVE_LIB=../siggui-native/netbeans/dist/Release/GNU-Linux-x86/libsiggui-native.so
mvn clean install -f ../zplot/pom.xml
mvn clean install
mkdir target/siggui-$VERSION
mkdir target/siggui-$VERSION/lib
cp $NATIVE_LIB target/siggui-$VERSION/lib/libsiggui-native.so
cp target/siggui-$VERSION-jar-with-dependencies.jar target/siggui-$VERSION
cp siggui.sh target/siggui-$VERSION
cd target
#tar -zcf siggui-$VERSION-linux.tar.gz siggui-$VERSION
zip -r siggui-$VERSION-linux.zip siggui-$VERSION
cd ..
