del src\javah
set CLASSPATH=../siggui/target/classes
"%JAVA_HOME%\bin\javah" -d src\javah ^
siggui.properties.SampleFormat ^
siggui.properties.WaveFileProperties ^
siggui.SigGuiNative ^
siggui.SigGuiController ^
siggui.timeseries.TimeseriesParameters ^
siggui.timeseries.TimeseriesTask ^
siggui.spectrum.SpectrumParameters ^
siggui.spectrum.SpectrumTask
