package zplot.transformers;

import zplot.utility.Interval;
import zplot.utility.Interval2D;
import zplot.utility.Interval2DTransform;

public class FixedScaleTransformer implements IDataTransformer {

	public FixedScaleTransformer(IDataTransformer delegate, Interval2D fixed) {
		this.delegate = delegate;
		this.fixed = fixed;
	}

	public FixedScaleTransformer(IDataTransformer delegate, Interval x, Interval y) {
		this.delegate = delegate;
		this.fixed = new Interval2D(x, y);
	}

	// Override dataEnvelope
	@Override
	public Interval2DTransform dataToCanvas(Interval2D dataEnvelope, Interval2D plotCanvas) {
		return ((IDataTransformer) delegate).dataToCanvas(fixedDataEnvelope(dataEnvelope), plotCanvas);
	}

	private Interval2D fixedDataEnvelope(Interval2D dataEnvelope) {
		return new Interval2D(fixed.x() != null ? fixed.x() : dataEnvelope.x(), fixed.y() != null ? fixed.y() : dataEnvelope.y());
	}

	private final IDataTransformer delegate;
	private final Interval2D fixed;
}
