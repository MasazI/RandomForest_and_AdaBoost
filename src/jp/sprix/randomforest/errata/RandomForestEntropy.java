package jp.sprix.randomforest.errata;

/**
 * 指定された事例を指定された分離ノードで分割した時のエントロピーを取得する
 * 
 * @author root
 * 
 */
public class RandomForestEntropy {
	/**
	 * 最大エントロピーのNode文字列を取得する
	 * 
	 * @param caseSubscripts
	 *            　サンプリング集合はRandomForestSampleCaseで保持している事例の添字で指定
	 * @param sampleCase
	 *            RandomForestSampleCase
	 * @param node
	 *            分離ノード
	 * @return
	 */
	public static double getEntropyDiff(int[] caseSubscripts, RandomForestErrataCase sampleCase,
			RandomForestNode node) {
		// if (caseSubscripts.length == 0) {
		// 分割不可能
		// return 0;
		// }

		int positiveCnt = 0;
		int negativeCnt = 0;
		int truePositiveCnt = 0;
		int trueNegativeCnt = 0;
		int falsePositiveCnt = 0;
		int falseNegativeCnt = 0;
		int decisionPositiveCnt = 0;
		int decisionNegativeCnt = 0;

		// 添字配列に属する集合の中で計算する
		for (int i = 0; i < caseSubscripts.length; i++) {
			// 添字
			int subscript = caseSubscripts[i];
			// 特徴量
			int features[] = sampleCase.features[subscript];
			if (sampleCase.positiveOrNegative[subscript] > 0) {
				// 正事例
				positiveCnt++;
				if (node.isAdaptiveFeature(features) > 0) {
					// nodeで正と判断
					truePositiveCnt++;
					decisionPositiveCnt++;
				} else {
					// nodeで負と判断
					falseNegativeCnt++;
					decisionNegativeCnt++;
				}
			} else {
				// 負事例
				negativeCnt++;
				if (node.isAdaptiveFeature(features) > 0) {
					// nodeで正と判断
					falsePositiveCnt++;
					decisionPositiveCnt++;
				} else {
					// nodeで負と判断
					trueNegativeCnt++;
					decisionNegativeCnt++;
				}
			}
		}

		// 全体Entropyの計算
		double allCnt = (double) positiveCnt + (double) negativeCnt;
		double entropy = -((((double) positiveCnt / allCnt) * (Math.log((double) positiveCnt
				/ allCnt))) - ((((double) negativeCnt / allCnt)) * (Math.log((double) negativeCnt
				/ allCnt))));

		// エントロピーの差
		double entropy_delta = 0;

		// 完全に分離できている場合、エントロピーの差計算は不要

		// 正事例と判断した集合のエントロピー
		double entropy_l = 0;
		double linerL = (double) decisionPositiveCnt / allCnt;
		if (decisionPositiveCnt != 0) {
			// positiveとカウントした事例が無い場合
			if (truePositiveCnt != 0) {
				if (falsePositiveCnt != 0) {
					entropy_l = -((double) truePositiveCnt / (double) decisionPositiveCnt)
							* (Math.log((double) truePositiveCnt / (double) decisionPositiveCnt))
							- ((double) falsePositiveCnt / (double) decisionPositiveCnt)
							* (Math.log((double) falsePositiveCnt / (double) decisionPositiveCnt));
				} else {
					entropy_l = 0;
				}
			} else {
				if (falsePositiveCnt != 0) {
					entropy_l = -((double) truePositiveCnt / (double) decisionPositiveCnt)
							* (Math.log((double) truePositiveCnt / (double) decisionPositiveCnt))
							- ((double) falsePositiveCnt / (double) decisionPositiveCnt)
							* (Math.log((double) falsePositiveCnt / (double) decisionPositiveCnt));
				} else {
					entropy_l = 0;
				}
			}
		}

		// 負事例と判断した集合のエントロピー
		double entropy_r = 0;
		double lineR = (double) decisionNegativeCnt / allCnt;
		if (decisionNegativeCnt != 0) {
			if (trueNegativeCnt != 0) {
				if (falseNegativeCnt != 0) {
					entropy_r = -((double) trueNegativeCnt / (double) decisionNegativeCnt)
							* (Math.log((double) trueNegativeCnt / (double) decisionNegativeCnt))
							- ((double) falseNegativeCnt / (double) decisionNegativeCnt)
							* (Math.log((double) falseNegativeCnt / (double) decisionNegativeCnt));
				} else {
					entropy_r = 0;
				}
			} else {
				if (falseNegativeCnt != 0) {
					entropy_r = -((double) trueNegativeCnt / (double) decisionNegativeCnt)
							* (Math.log((double) trueNegativeCnt / (double) decisionNegativeCnt))
							- ((double) falseNegativeCnt / (double) decisionNegativeCnt)
							* (Math.log((double) falseNegativeCnt / (double) decisionNegativeCnt));
				} else {
					entropy_r = 0;
				}
			}
		}

		entropy_delta = entropy - ((entropy_l * linerL) + (entropy_r * lineR));

		// debug
		// System.out.println(node.output() + "entropy: " + entropy_delta);

		// 絶対値
		return entropy_delta;
	}
}
