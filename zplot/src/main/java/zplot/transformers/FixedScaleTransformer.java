package zplot.transformers;

import zplot.utility.Interval;
import zplot.utility.Interval2D;
import zplot.utility.Interval2DTransform;

public class FixedScaleTransformer implements DataTransformer {

	public FixedScaleTransformer(DataTransformer delegate, Interval2D fixed) {
		this.delegate = delegate;
		this.fixed = fixed;
	}

	public FixedScaleTransformer(DataTransformer delegate, Interval x, Interval y) {
		this.delegate = delegate;
		this.fixed = new Interval2D(x, y);
	}

	// Override dataEnvelope
	@Override
	public Interval2DTransform dataToCanvas(Interval2D dataEnvelope, Interval2D plotCanvas) {
		return ((DataTransformer) delegate).dataToCanvas(fixedDataEnvelope(dataEnvelope), plotCanvas);
	}

	private Interval2D fixedDataEnvelope(Interval2D dataEnvelope) {
		return new Interval2D(fixed.x() != null ? fixed.x() : dataEnvelope.x(), fixed.y() != null ? fixed.y() : dataEnvelope.y());
	}

	private final DataTransformer delegate;
	private final Interval2D fixed;
}
