package zplot.transformers;

import zplot.utility.Interval2D;
import zplot.utility.Interval2DTransform;

public class SquareAxisTransformer implements DataTransformer {

	public SquareAxisTransformer(int xPad, int yPad) {
		this.xPad = xPad;
		this.yPad = yPad;
	}

	public SquareAxisTransformer() {
		this.xPad = 5;
		this.yPad = 5;
	}

	public Interval2D axisDisplayRange(Interval2D dataEnvelope, Interval2D plotCanvas) {
		double dataWidth = dataEnvelope.width();
		double dataHeight = dataEnvelope.height();
		if (dataWidth == 0.0 || dataHeight == 0.0) {
			return dataEnvelope;
		}
		double scale = Math.min(plotCanvas.width() / dataWidth, plotCanvas.height() / dataHeight);

		// pixels |--> data units
		double width = plotCanvas.width() / scale;
		double height = plotCanvas.height() / scale;

		double widthExcess = width - dataWidth;
		double heightExcess = height - dataHeight;

		double xBegin = dataEnvelope.x().begin() - (widthExcess / 2.0);
		double yBegin = dataEnvelope.y().begin() - (heightExcess / 2.0);

		return new Interval2D(xBegin, xBegin + width, yBegin, yBegin + height);
	}

	@Override
	public Interval2DTransform dataToCanvas(Interval2D dataEnvelope, Interval2D plotCanvas) {
		return new FillCanvasTransformer(xPad, yPad).dataToCanvas(axisDisplayRange(dataEnvelope, plotCanvas), plotCanvas);
	}

	private final int yPad;
	private final int xPad;
}
