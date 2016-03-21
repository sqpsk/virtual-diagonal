@ECHO OFF
SET INSTALL_DIR=%~dp0
SET PATH=%INSTALL_DIR%lib;%PATH%
java -ea -Xmx512M -jar %INSTALL_DIR%siggui-1.0.0-jar-with-dependencies.jar