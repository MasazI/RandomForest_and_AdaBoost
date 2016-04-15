package jp.sprix.randomforest;

/**
 * サンプリングの分割結果を保持するクラス
 * 
 * @author root
 * 
 */
public class RandomForestDivideResult {
	// 分割あとの正事例添字
	private int[] positiveSubscripts = null;

	// 分割あとの負事例添字
	private int[] negativeSubscripts = null;

	public int[] getPositiveSubscripts() {
		return positiveSubscripts;
	}

	public void setPositiveSubscripts(int[] positiveSubscripts) {
		this.positiveSubscripts = positiveSubscripts;
	}

	public int[] getNegativeSubscripts() {
		return negativeSubscripts;
	}

	public void setNegativeSubscripts(int[] negativeSubscripts) {
		this.negativeSubscripts = negativeSubscripts;
	}

}
