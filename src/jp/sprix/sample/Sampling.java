package jp.sprix.sample;

import java.util.Random;

/**
 * サンプリングクラス
 * 
 * @author root
 * 
 */
public class Sampling {
	private Random random = null;

	public Sampling(int seed) {
		random = new Random(seed);
	}

	/**
	 * サンプリング集合用の添字を返す
	 * 
	 * @param cnt
	 *            サンプリング集合の要素の個数
	 * @param max
	 *            サンプリング用添字のmax(0～max)
	 * @param seed
	 *            乱数生成の種
	 * @return int[] サンプリング用添字番号
	 */
	public static int[] sampling(int cnt, int max, int seed) {
		Random random = new Random(seed);
		int[] samplingNumber = new int[cnt];
		for (int i = 0; i < cnt; i++) {
			samplingNumber[i] = random.nextInt(max);
		}
		return samplingNumber;
	}

	/**
	 * 範囲とseedを指定してランダムな整数を取得する
	 * 
	 * @return
	 */
	public int sampling(int max) {
		return random.nextInt(max);
	}

}
