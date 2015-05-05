package siggui.rangecontrols;

import siggui.utility.SigMath;

public class BlockCoverRangeModel extends BoundedRangeModel {

	public static BlockCoverRangeModel make(
			long begin,
			long end,
			long maxSize,
			int blockSize,
			int blockStep) {
		return new BlockCoverRangeModel(begin, end, blockSize, maxSize, blockSize, blockSize);
	}

	public BlockCoverRangeModel(
			long begin,
			long end,
			long minSize,
			long fileSize,
			int blockSize,
			int blockStep) {
		super(fileSize, minSize, fileSize, begin, end);
		this.blockSize = blockSize;
		this.blockStep = blockStep;
	}

	public BlockCoverRangeModel(BlockCoverRangeModel other) {
		this(other.begin, other.end, other.minSize, other.fileSize, other.blockSize, other.blockStep);
		this.isAdjusting = other.isAdjusting;
	}

	private long roundSize(long size) {
		long blockCount = 1 + (size - blockSize) / blockStep;
		return SigMath.coverSize(blockSize, blockStep, blockCount);
	}

	public void setBlockSizeAndStep(int blockSize, int blockStep, long begin, long end) {
		BlockCoverRangeModel oldValue = new BlockCoverRangeModel(this);
		this.begin = begin;
		this.end = end;
		this.minSize = blockSize;
		this.blockSize = blockSize;
		this.blockStep = blockStep;
		assert isValid();
		changeSupport.firePropertyChange("range", oldValue, this);
	}

	@Override
	public void setBeginSameEndPriorityEnd(long newBegin) {
		newBegin = SigMath.bound(0, newBegin, end - minSize);
		long size = roundSize(end - newBegin);
		setRange(end - size, end);
	}

	@Override
	public void setEndSameBeginPriorityBegin(long newEnd) {
		newEnd = SigMath.bound(begin + minSize, newEnd, fileSize);
		long size = roundSize(newEnd - begin);
		setRange(begin, begin + size);
	}

	@Override
	public void setSizeSameBeginPriorityBegin(long size) {
		size = SigMath.bound(minSize, size, fileSize);
		size = roundSize(size);
		setRange(begin, begin + size);
	}

	@Override
	public void setBeginSameSizePriorityBegin(long newBegin) {
		newBegin = SigMath.bound(0, newBegin, fileSize - getSize());
		setRange(newBegin, newBegin + getSize());
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 31 * hash + this.blockSize;
		hash = 31 * hash + this.blockStep;
		hash = 31 * hash + (this.isAdjusting ? 1 : 0);
		hash = 31 * hash + (int) (this.fileSize ^ (this.fileSize >>> 32));
		hash = 31 * hash + (int) (this.minSize ^ (this.minSize >>> 32));
		hash = 31 * hash + (int) (this.maxSize ^ (this.maxSize >>> 32));
		hash = 31 * hash + (int) (this.begin ^ (this.begin >>> 32));
		hash = 31 * hash + (int) (this.end ^ (this.end >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && getClass() == obj.getClass() && compareFields((BlockCoverRangeModel) obj);
	}

	protected boolean compareFields(BlockCoverRangeModel other) {
		return compareFields((BoundedRangeModel) other)
				&& this.blockSize == other.blockSize
				&& this.blockStep == other.blockStep;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append(" blockSize ").append(blockSize).append(" blockStep ").append(blockStep);
		return sb.toString();
	}
	private int blockSize;
	private int blockStep;
}
