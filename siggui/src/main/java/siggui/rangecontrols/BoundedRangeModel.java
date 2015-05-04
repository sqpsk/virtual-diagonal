package siggui.rangecontrols;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import siggui.utility.SigMath;

public class BoundedRangeModel implements IRangeModel {

    public static BoundedRangeModel makeChecked(long fileSize, long minSize, long maxSize, long begin, long end) {
        maxSize = Math.min(maxSize, fileSize);
        minSize = Math.min(minSize, maxSize);
        return new BoundedRangeModel(fileSize, minSize, maxSize, begin, end);
    }
    
    public static BoundedRangeModel make(long fileSize, long minSize, long maxSize, long begin, long end) {
        BoundedRangeModel m = new BoundedRangeModel(fileSize, minSize, maxSize, begin, end);
        assert m.isValid();
        return m;
    }

    // Represents a non-empty half open range [begin, end)
    // We allow begin = end (empty range).
    protected BoundedRangeModel(long fileSize, long minSize, long maxSize, long begin, long end) {
        this.fileSize = fileSize;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.begin = begin;
        this.end = end;
    }

    public BoundedRangeModel(long fileSize) {
        this(fileSize, 0, fileSize, 0, fileSize);
    }

    public BoundedRangeModel(BoundedRangeModel other) {
        this(other.fileSize, other.minSize, other.maxSize, other.begin, other.end);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener("range", l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener("range", l);
    }

    @Override
    public boolean isAdjusting() {
        return isAdjusting;
    }
    
    @Override
    public long getFileSize() {
        return fileSize;
    }

    @Override
    public long getMaxSize() {
        return maxSize;
    }

    @Override
    public long getMinSize() {
        return minSize;
    }

    @Override
    public long getBegin() {
        return begin;
    }

    @Override
    public long getEnd() {
        return end;
    }

    @Override
    public long getSize() {
        return end - begin;
    }

    @Override
    public void setIsAdjusting(boolean newValue) {
        boolean oldValue = isAdjusting;
        isAdjusting = newValue;
        changeSupport.firePropertyChange("range", oldValue, newValue);
    }

    @Override
    public void setRange(long begin, long end) {
        BoundedRangeModel oldValue = new BoundedRangeModel(this);
        this.begin = begin;
        this.end = end;
        assert isValid();
        changeSupport.firePropertyChange("range", oldValue, this);
    }

    @Override
    public void setBeginSameEndPriorityEnd(long newBegin) {
        newBegin = SigMath.bound(Math.max(0, end - maxSize), newBegin, end - minSize);
        setRange(newBegin, end);
    }

    @Override
    public void setEndSameBeginPriorityBegin(long newEnd) {
        newEnd = SigMath.bound(begin + minSize, newEnd, Math.min(begin + maxSize, fileSize));
        setRange(begin, newEnd);
    }

    @Override
    public void setSizeSameBeginPriorityBegin(long size) {
        size = SigMath.bound(minSize, size, maxSize);
        long newEnd = Math.min(begin + size, fileSize);
        long newBegin = newEnd - size;
        setRange(newBegin, newEnd);
    }

    @Override
    public void setBeginSameSizePriorityBegin(long newBegin) {
        newBegin = SigMath.bound(0, newBegin, fileSize - getSize());
        setRange(newBegin, newBegin + getSize());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.isAdjusting ? 1 : 0);
        hash = 97 * hash + (int) (this.fileSize ^ (this.fileSize >>> 32));
        hash = 97 * hash + (int) (this.minSize ^ (this.minSize >>> 32));
        hash = 97 * hash + (int) (this.maxSize ^ (this.maxSize >>> 32));
        hash = 97 * hash + (int) (this.begin ^ (this.begin >>> 32));
        hash = 97 * hash + (int) (this.end ^ (this.end >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass() == obj.getClass() && compareFields((BoundedRangeModel) obj);
    }
    
    protected boolean compareFields(BoundedRangeModel other) {
        return other != null && isAdjusting == other.isAdjusting
                && fileSize == other.fileSize
                && minSize == other.minSize
                && maxSize == other.maxSize
                && begin == other.begin
                && end == other.end;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(begin).append(", ").append(end).append(')');
        sb.append(" size ").append(getSize());
        sb.append(" / ").append(fileSize);
        sb.append(" isAdjusting ").append(isAdjusting);
        return sb.toString();
    }

    protected boolean isValid() {
        boolean validSizeBounds = minSize >= 0 && minSize <= maxSize && maxSize <= fileSize;
        if (!validSizeBounds) {
            System.out.println("ERROR: INVALID size bounds " + this);
        }
        boolean validRegion = begin >= 0 && begin < end && end <= fileSize;
        if (!validRegion) {
            System.out.println("ERROR: INVALID region " + this);
        }
        long size = end - begin;
        boolean validRegionSize = size >= minSize && minSize <= maxSize;
        if (!validRegionSize) {
            System.out.println("ERROR: INVALID region size " + this);
        }
        return validSizeBounds && validRegion && validRegionSize;
    }
    
    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    protected boolean isAdjusting = false;
    protected long fileSize;
    protected long minSize;
    protected long maxSize;
    protected long begin;
    protected long end;
}
