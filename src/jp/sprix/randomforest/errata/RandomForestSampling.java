package jp.sprix.randomforest.errata;

/**
 * ランダムフォレストのサンプリング用添字を保持するクラス
 * 
 * @author root
 * 
 */
public class RandomForestSampling {
	/* サンプリングの添字番号配列 [tree番号][添字配列] */
	private int[][] subscripts = null;

	/**
	 * コンストラクタ
	 * 
	 * @param cnt
	 *            サンプリング集合（木）の数
	 */
	public RandomForestSampling(int cnt) {
		subscripts = new int[cnt][];
	}

	/**
	 * サンプリング集合の添字を追加する
	 * 
	 * @param subscript
	 */
	public void addSampling(int treeNum, int[] subscript) {
		subscripts[treeNum] = subscript;
	}

	/**
	 * サンプリング集合の添字を取得する
	 * 
	 * @return
	 */
	public int[] getSampling(int treeNum) {
		return subscripts[treeNum];
	}
}
