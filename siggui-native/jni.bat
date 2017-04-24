del src\javah
set CLASSPATH=../siggui/target/classes
"%JAVA_HOME%\bin\javah" -d src\javah ^
siggui.properties.SampleFormat ^
siggui.properties.WaveFileProperties ^
siggui.SigGuiNative ^
siggui.SigGuiController ^
siggui.perspectives.timeseries.TimeseriesParameters ^
siggui.perspectives.timeseries.TimeseriesTask ^
siggui.perspectives.spectrum.SpectrumParameters ^
siggui.perspectives.spectrum.SpectrumTask
