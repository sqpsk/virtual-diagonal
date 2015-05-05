package zplot.transformers;

import zplot.utility.Interval2D;
import zplot.utility.Interval2DTransform;
import zplot.utility.IntervalTransform;

public class FillCanvasTransformer implements IDataTransformer {

	public FillCanvasTransformer(int xPad, int yPad) {
		this.xPad = xPad;
		this.yPad = yPad;
	}

	public FillCanvasTransformer() {
		this.xPad = 0;
		this.yPad = 5;
	}

	@Override
	public Interval2DTransform dataToCanvas(Interval2D dataEnvelope, Interval2D plotCanvas) {
		int xp = plotCanvas.x().size() > 8 * xPad ? xPad : 0;
		int yp = plotCanvas.y().size() > 8 * yPad ? yPad : 0;
		IntervalTransform x = new IntervalTransform(
				dataEnvelope.x().begin(),
				dataEnvelope.x().end(),
				plotCanvas.x().begin() + xp,
				plotCanvas.x().end() - xp);
		IntervalTransform y = new IntervalTransform(
				dataEnvelope.y().begin(),
				dataEnvelope.y().end(),
				plotCanvas.y().end() - yp,
				plotCanvas.y().begin() + yp);
		return new Interval2DTransform(x, y);
	}

	private final int yPad;
	private final int xPad;
}
