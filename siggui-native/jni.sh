rm -rf src/javah
mkdir src/javah
javah -d src/javah -classpath ../siggui/target/classes \
siggui.properties.WaveFileProperties \
siggui.properties.SampleFormat \
siggui.SigGuiNative \
siggui.SigGuiController \
siggui.timeseries.TimeseriesParameters \
siggui.timeseries.TimeseriesTask \
siggui.spectrum.SpectrumParameters \
siggui.spectrum.SpectrumTask
