package jp.sprix.adaboost;

public class FinalClassifier {
	/**
	 * 閾値配列
	 */
	private String[] thresholdStrArray = null;

	/**
	 * 閾値の線形結合係数
	 */
	private double[] linearCoefficients = null;

	// getter and setter

	public String[] getThresholdStrArray() {
		return thresholdStrArray;
	}

	public void setThresholdStrArray(String[] thresholdStrArray) {
		this.thresholdStrArray = thresholdStrArray;
	}

	public double[] getLinearCoefficients() {
		return linearCoefficients;
	}

	public void setLinearCoefficients(double[] linearCoefficients) {
		this.linearCoefficients = linearCoefficients;
	}

}
