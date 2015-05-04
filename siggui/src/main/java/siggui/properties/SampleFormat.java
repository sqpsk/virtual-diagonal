package siggui.properties;

public class SampleFormat {

    public static final int SAMPLE_TYPE_TWOS = 0;
    public static final int SAMPLE_TYPE_OFFSET = 1;
    public static final int SAMPLE_TYPE_FLOAT = 2;

    public static SampleFormat make(boolean isComplex, int sampleType, int bitsPerSample, boolean isLittleEndian) {
        boolean valid =
                (sampleType == SAMPLE_TYPE_FLOAT && (bitsPerSample == 32 || bitsPerSample == 64))
                || bitsPerSample == 8
                || bitsPerSample == 16
                || bitsPerSample == 32
                || bitsPerSample == 64;

        if (valid) {
            return new SampleFormat(isComplex, sampleType, bitsPerSample, isLittleEndian);
        } else {
            return null;
        }
    }

    private SampleFormat(boolean isComplex, int sampleType, int bitsPerSample, boolean isLittleEndian) {
        this.isComplex = isComplex;
        this.sampleType = sampleType;
        this.bitsPerSample = bitsPerSample;
        this.isLittleEndian = isLittleEndian;
    }

    public int bytesPerSample() {
        int containerBytes = (bitsPerSample + 7) / 8;
        return isComplex ? 2 * containerBytes : containerBytes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(48);
        sb.append(isComplex ? "Complex " : "Real ");
        sb.append(bitsPerSample).append("-bit ");
        switch (sampleType) {
            case SAMPLE_TYPE_FLOAT:
                sb.append("float");
                break;
            case SAMPLE_TYPE_TWOS:
                sb.append("twos-complement");
                break;
            case SAMPLE_TYPE_OFFSET:
                sb.append("offset-binary");
                break;
            default:
                sb.append("sampleType").append(sampleType);
                break;
        }

        if (isLittleEndian) {
            sb.append(" little-endian");
        } else {
            sb.append(" big-endian");
        }

        return sb.toString();
    }
    public final boolean isComplex;
    public final int sampleType;
    public final int bitsPerSample;
    public final boolean isLittleEndian;
}
