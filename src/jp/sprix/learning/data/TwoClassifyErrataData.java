package jp.sprix.learning.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import jp.sprix.component.ComponentErrataData;
import jp.sprix.cv.CVErrataGeometry;
import jp.sprix.io.Import;
import jp.sprix.io.Output;

/**
 * フィルターを通して2つに分類したデータクラス
 * 
 * @author root
 * 
 */
public class TwoClassifyErrataData implements TwoClassifyData {
	private double weightRecall = 0;

	/**
	 * 正解に分類したリスト(本当に正解)
	 */
	private ArrayList<String[]> truePositiveSampleList = new ArrayList<String[]>();

	/**
	 * 正解に分類したリスト(本当は不正解)
	 */
	private ArrayList<String[]> falsePositiveSampleList = new ArrayList<String[]>();

	/**
	 * 不正解に分類したリスト(本当に不正解)
	 */
	private ArrayList<String[]> trueNegativeSampleList = new ArrayList<String[]>();

	/**
	 * 不正解に分類したリスト(本当に正解)
	 */
	private ArrayList<String[]> falseNegativeSampleList = new ArrayList<String[]>();

	/**
	 * F値を取得する
	 * 
	 * @param wRecall
	 *            Recallの重み
	 * @return f-measure
	 */
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
	 * Recallを取得する
	 * 
	 * @return recall
	 */
	public double getRecall() {
		if ((getTruePositiveSampleListCnt() + getFalseNegativeSampleListCnt()) == 0) {
			return 0;
		}
		return getTruePositiveSampleListCnt()
				/ (getTruePositiveSampleListCnt() + getFalseNegativeSampleListCnt());
	}

	/**
	 * Precisionを取得する
	 * 
	 * @return precision
	 */
	public double getPrecision() {
		if ((getTruePositiveSampleListCnt() + getFalsePositiveSampleListCnt()) == 0) {
			return 0;
		}
		return getTruePositiveSampleListCnt()
				/ (getTruePositiveSampleListCnt() + getFalsePositiveSampleListCnt());
	}

	// reset
	public void reset() {
		truePositiveSampleList.clear();
		falsePositiveSampleList.clear();
		trueNegativeSampleList.clear();
		falseNegativeSampleList.clear();
	}

	// add
	public void addPositiveSampleList(String[] sample, String type) {
		if (type.equals("+")) {
			truePositiveSampleList.add(sample);
		} else {
			falsePositiveSampleList.add(sample);
		}
	}

	public void addNegativeSampleList(String[] sample, String type) {
		if (type.equals("+")) {
			falseNegativeSampleList.add(sample);
		} else {
			trueNegativeSampleList.add(sample);
		}
	}

	// cnt

	public double getTruePositiveSampleListCnt() {
		return truePositiveSampleList.size();
	}

	public double getFalsePositiveSampleListCnt() {
		return falsePositiveSampleList.size();
	}

	public double getTrueNegativeSampleListCnt() {
		return trueNegativeSampleList.size();
	}

	public double getFalseNegativeSampleListCnt() {
		return falseNegativeSampleList.size();
	}

	// getter and setter
	public double getWeightRecall() {
		return weightRecall;
	}

	public void setWeightRecall(double weightRecall) {
		this.weightRecall = weightRecall;
	}

	/**
	 * 戻り値に含まれているデータと最終的に返答対象になるデータを出力する
	 * 
	 * @return
	 */
	public String getConclusiveAnswer(ComponentErrataData componentData) {
		StringBuilder sb = new StringBuilder();
		Properties prop = Import.importProperty();
		String filePath = prop.getProperty("conclusive.file.path");
		double scale = Double.parseDouble(prop.getProperty("train.query.scale"));

		int allPositive = componentData.getSamplePositive();
		// 最終的な回答のMap <fileName, { + or - , Score }>
		HashMap<String, String[]> conclusiveAnswerMap = new HashMap<String, String[]>();
		// 最終的な回答Map中の正解数
		int conclusiveCorrectAnswerCnt = 0;

		// 本当の正解を書き出す
		for (String[] truePositive : truePositiveSampleList) {
			String fileName = truePositive[0];
			String rank = truePositive[1];
			String type = truePositive[truePositive.length - 1];
			String score = truePositive[truePositive.length - 2];

			conclusiveAnswerMap.put(fileName, new String[] { type, score, rank });
			conclusiveCorrectAnswerCnt++;
			sb.append(fileName + "\t" + type + "\t" + score + "\t" + rank + "\t"
					+ CVErrataGeometry.getOutputString(truePositive, scale));
			sb.append("\n");
		}

		// 本当は不正解だが、Positiveになったものを書き出す。重複していてスコアが高い場合には、本当の正解を上書きする
		for (String[] falsePositive : falsePositiveSampleList) {
			String fileName = falsePositive[0];
			String rank = falsePositive[1];
			String type = falsePositive[falsePositive.length - 1];
			String score = falsePositive[falsePositive.length - 2];

			if (conclusiveAnswerMap.containsKey(fileName)) {
				String[] value = conclusiveAnswerMap.get(fileName);
				String true_score = value[1];
				if (Integer.parseInt(true_score) < Integer.parseInt(score)) {
					conclusiveAnswerMap.put(fileName, new String[] { type, score, rank });
					sb.append(fileName + "\t" + type + "\t" + score + "\t" + rank + "\t"
							+ CVErrataGeometry.getOutputString(falsePositive, scale) + "\t"
							+ "overwrite");
					sb.append("\n");
					conclusiveCorrectAnswerCnt--;
				}
			} else {
				conclusiveAnswerMap.put(fileName, new String[] { type, score, rank });
				sb.append(fileName + "\t" + type + "\t" + score + "\t" + rank + "\t"
						+ CVErrataGeometry.getOutputString(falsePositive, scale));
				sb.append("\n");
			}
		}
		sb.append(conclusiveCorrectAnswerCnt + "\t" + allPositive);
		sb.append("\n");
		Output.outputFile(filePath, sb);
		return conclusiveCorrectAnswerCnt + "\t" + allPositive;
	}
}
