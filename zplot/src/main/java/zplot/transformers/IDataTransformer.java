package zplot.transformers;

import zplot.utility.Interval2D;
import zplot.utility.Interval2DTransform;

public interface IDataTransformer {

	Interval2DTransform dataToCanvas(Interval2D dataEnvelope, Interval2D plotCanvas);

}
