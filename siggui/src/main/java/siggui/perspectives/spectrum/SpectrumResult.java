package siggui.perspectives.spectrum;

class SpectrumResult {

	public float[] getPower() {
		return power;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName());
		sb.append(": size=").append(power.length);
		return sb.toString();
	}

	private float[] power = null;
}
