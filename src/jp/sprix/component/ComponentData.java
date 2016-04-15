package jp.sprix.component;

public class ComponentData {
	/**
	 * 全サンプル数
	 */
	private int sampleNumber = 0;

	/**
	 * 全Positiveを期待する要素数
	 */
	private int samplePositive = 0;

	/**
	 * 全Negativeを期待する要素数
	 */
	private int sampleNegative = 0;

	// インクリメント

	public void setSamplePositiveInc() {
		this.samplePositive++;
	}

	public void setSampleNegativeInc() {
		this.sampleNegative++;
	}

	// getter and setter

	public int getSampleNumber() {
		return sampleNumber;
	}

	public void setSampleNumber(int sampleNumber) {
		this.sampleNumber = sampleNumber;
	}

	public int getSamplePositive() {
		return samplePositive;
	}

	public void setSamplePositive(int samplePositive) {
		this.samplePositive = samplePositive;
	}

	public int getSampleNegative() {
		return sampleNegative;
	}

	public void setSampleNegative(int sampleNegative) {
		this.sampleNegative = sampleNegative;
	}
}
