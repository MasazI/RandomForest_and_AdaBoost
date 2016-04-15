package jp.sprix.learning.data;

import jp.sprix.adaboost.TrainCaseData;
import jp.sprix.io.Output;

public class TwoClassifyAnalyze implements TwoClassifyData {
	private int truePositiveCnt = 0;
	private int trueNegativeCnt = 0;
	private int falsePositiveCnt = 0;
	private int falseNegativeCnt = 0;

	/**
	 * コンストラクタ
	 */
	public TwoClassifyAnalyze() {

	}

	/**
	 * コンストラクタ
	 * 
	 * @param trainCasePointData
	 * @param finalClassify
	 * @param componentPointData
	 */
	public TwoClassifyAnalyze(TrainCaseData caseData, int[] finalClassify) {
		String filePath = "/home/adaboost/detail_result.txt";
		StringBuilder sb = new StringBuilder();
		String[] sampleNames = caseData.getSampleNames();
		int[] twoClasses = caseData.getTwoclasses();
		for (int i = 0; i < twoClasses.length; i++) {

			if (twoClasses[i] == 1) {
				if (twoClasses[i] == finalClassify[i]) {
					truePositiveCnt++;
					sb.append(sampleNames[i]);
					sb.append("\t");
					sb.append("1");
					sb.append("\t");
					sb.append("1");
				} else {
					falseNegativeCnt++;
					sb.append(sampleNames[i]);
					sb.append("\t");
					sb.append("1");
					sb.append("\t");
					sb.append("0");
				}
			} else if (twoClasses[i] == -1) {
				if (twoClasses[i] == finalClassify[i]) {
					trueNegativeCnt++;
					sb.append(sampleNames[i]);
					sb.append("\t");
					sb.append("0");
					sb.append("\t");
					sb.append("0");
				} else {
					falsePositiveCnt++;
					sb.append(sampleNames[i]);
					sb.append("\t");
					sb.append("0");
					sb.append("\t");
					sb.append("1");
				}
			}
			sb.append("\r\n");
		}
		Output.outputFile(filePath, sb);
	}

	/**
	 * F値を返す
	 * 
	 * @param recallの重み
	 */
	@Override
	public double getFMeasure(double wRecall) {
		if (wRecall < 0 & 1 < wRecall) {
			return 0;
		}

		if (getPrecision() == 0 || getRecall() == 0) {
			return 0;
		}

		double invP = 1 / getPrecision();
		double invR = 1 / getRecall();
		double denominator = (wRecall * invP) + ((1 - wRecall) * invR);
		return 1 / denominator;
	}

	/**
	 * Recallを返す
	 */
	@Override
	public double getRecall() {
		if (((double) truePositiveCnt + (double) falseNegativeCnt) == 0) {
			return 0;
		}

		return (double) truePositiveCnt / ((double) truePositiveCnt + (double) falseNegativeCnt);
	}

	/**
	 * Precisionを返す
	 */
	@Override
	public double getPrecision() {
		if (((double) truePositiveCnt + (double) falsePositiveCnt) == 0) {
			return 0;
		}

		return (double) truePositiveCnt / ((double) truePositiveCnt + (double) falsePositiveCnt);
	}

	// getter and setter

	public int getTruePositiveCnt() {
		return truePositiveCnt;
	}

	public void setTruePositiveCnt(int truePositiveCnt) {
		this.truePositiveCnt = truePositiveCnt;
	}

	public int getTrueNegativeCnt() {
		return trueNegativeCnt;
	}

	public void setTrueNegativeCnt(int trueNegativeCnt) {
		this.trueNegativeCnt = trueNegativeCnt;
	}

	public int getFalsePositiveCnt() {
		return falsePositiveCnt;
	}

	public void setFalsePositiveCnt(int falsePositiveCnt) {
		this.falsePositiveCnt = falsePositiveCnt;
	}

	public int getFalseNegativeCnt() {
		return falseNegativeCnt;
	}

	public void setFalseNegativeCnt(int falseNegativeCnt) {
		this.falseNegativeCnt = falseNegativeCnt;
	}

}
