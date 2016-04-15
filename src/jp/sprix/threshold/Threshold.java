package jp.sprix.threshold;

public interface Threshold {
	public void next();

	public boolean isFinish();

	public void resetThreshold();

	public String output();

	public void importThreshold(String thresholdStr);

	public String getType();

}
