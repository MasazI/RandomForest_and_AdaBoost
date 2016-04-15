package jp.sprix.learning.data;

public interface TwoClassifyData {
	/**
	 * F値を取得する
	 * 
	 * @param wRecall
	 *            Recallの重み
	 * @return
	 */
	public double getFMeasure(double wRecall);

	/**
	 * Recallを取得する
	 * 
	 * @return recall
	 */
	public double getRecall();

	/**
	 * Precisionを取得する
	 * 
	 * @return precision
	 */
	public double getPrecision();
}
