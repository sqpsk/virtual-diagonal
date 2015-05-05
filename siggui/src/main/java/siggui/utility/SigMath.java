package siggui.utility;

public class SigMath {

	// Returns the smallest integer N >= 0 st x < 2^N
	public static int log2Bound(long x) {
		long y = 1;
		int yLog2 = 0;
		while (y <= x) {
			y <<= 1;
			++yLog2;
		}
		return yLog2;
	}

	public static int roundPositive(double x) {
		return (int) (x + 0.5);
	}

	public static long bound(long begin, long x, long end) {
		assert begin <= end;
		return Math.min(Math.max(begin, x), end);
	}

	public static int bound(int begin, int x, int end) {
		assert begin <= end;
		return Math.min(Math.max(begin, x), end);
	}

	public static long coverSize(long blockSize, long blockStep, long blockCount) {
		return blockSize + (blockCount - 1) * blockStep;
	}

	public static int coverSize(int blockSize, int blockStep, int blockCount) {
		return blockSize + (blockCount - 1) * blockStep;
	}

	// Return the maximum blockCount such that 
	// coverSize(blockSize, blockStep, blockCount) <= coverSizeBound
	public static int maxBlockCount(int blockSize, int blockStep, int coverSizeBound) {
		return 1 + (coverSizeBound - blockSize) / blockStep;
	}
}
