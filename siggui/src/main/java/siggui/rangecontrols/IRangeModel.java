package siggui.rangecontrols;

import java.beans.PropertyChangeListener;

public interface IRangeModel {

    void addPropertyChangeListener(PropertyChangeListener l);

    void removePropertyChangeListener(PropertyChangeListener l);
    
    boolean isAdjusting();
    
    long getFileSize();

    long getMaxSize();

    long getMinSize();

    long getBegin();

    long getEnd();

    long getSize();
    
    void setRange(long begin, long end);
    
    void setBeginSameSizePriorityBegin(long begin);
    
    void setEndSameBeginPriorityBegin(long end);
    
    void setSizeSameBeginPriorityBegin(long size);
    
    void setBeginSameEndPriorityEnd(long begin);
    
    void setIsAdjusting(boolean b);
}
