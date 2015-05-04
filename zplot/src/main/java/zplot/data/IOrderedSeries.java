package zplot.data;

public interface IOrderedSeries extends ISeries {
    
    // Returns max i such that x(i) <= x
    int pullBack(double x);
}
