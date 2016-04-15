package jp.sprix.randomforest.errata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import jp.sprix.component.ComponentErrataData;
import jp.sprix.feature.FeatureErrata;
import jp.sprix.io.Output;
import jp.sprix.io.Utils;
import jp.sprix.learning.data.TwoClassifyAnalyze;

/**
 * ランダムフォレストのerrata事例クラス
 * 
 * 事例=Errata
 * 
 * @author root
 * 
 */
public class RandomForestErrataCase {
	/* 事例(errata)配列 */
	String[][] cases = null;

	/* 特徴量配列[添字][特徴量配列] */
	int[][] features = null;

	/* positive 1 or negative -1 */
	int[] positiveOrNegative = null;

	/* classify(分類結果) positive 1 or negative -1 */
	int[] classify = null;

	/* positive cnt */
	private int positiveCnt = 0;

	/* negative cnt */
	private int negativeCnt = 0;

	/*  */
	private int randomforest_sampling_max = 0;

	/**
	 * errataの事例
	 * 
	 * @param componentErrataData
	 * @param scale
	 */
	public RandomForestErrataCase(ComponentErrataData componentErrataData) {
		// 事例数によって配列の大きさを決める
		int cnt = componentErrataData.getSampleNumber();
		cases = new String[cnt][];
		features = new int[cnt][];
		positiveOrNegative = new int[cnt];

		// 事例集合を取得<[errata行のタブ区切り配列], + or ->
		HashMap<String[], String> samples = componentErrataData.getSamples();
		// 添字
		int i = 0;
		for (Entry<String[], String> sample : samples.entrySet()) {
			// 正・負事例
			String type = sample.getValue();
			// errata
			String[] errataLine = sample.getKey();
			// errataに対する特徴量
			double[] feature = FeatureErrata.getFeature(errataLine);
			// 点
			cases[i] = errataLine;
			// 特徴量
			features[i] = FeatureErrata.scaleInteger(feature);
			// 正負判定
			if (type.equals("+")) {
				positiveOrNegative[i] = 1;
				positiveCnt++;
			} else if (type.equals("-")) {
				positiveOrNegative[i] = -1;
				negativeCnt++;
			}
			// 添字の更新
			i++;
		}
		randomforest_sampling_max = i;
		System.out.println("事例数: " + (i + 1));
	}

	/**
	 * 指定された添字配列の事例がすべて正事例かどうか
	 * 
	 * @param subscripts
	 *            事例の添字配列
	 * @return すべて正事例 true、負事例を含むfalse
	 */
	public boolean isAllPositive(int[] subscripts) {
		for (int i = 0; i < subscripts.length; i++) {
			if (positiveOrNegative[subscripts[i]] < 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 添字配列が表す事例がpositiveかどうか？
	 * 
	 * @param subscripts
	 * @return
	 */
	public int positiveCnt(int[] subscripts) {
		int cnt = 0;
		for (int i = 0; i < subscripts.length; i++) {
			if (positiveOrNegative[subscripts[i]] > 0) {
				cnt++;
			}
		}
		return cnt;
	}

	/**
	 * 指定された添字配列の事例がすべて負事例かどうか
	 * 
	 * @param subscripts
	 *            　事例の添字配列
	 * @return すべて負事例 true、正事例を含むfalse
	 */
	public boolean isAllNegative(int[] subscripts) {
		for (int i = 0; i < subscripts.length; i++) {
			if (positiveOrNegative[subscripts[i]] > 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 添字配列が表す事例がpositiveかどうか？
	 * 
	 * @param subscripts
	 * @return
	 */
	public int negativeCnt(int[] subscripts) {
		int cnt = 0;
		for (int i = 0; i < subscripts.length; i++) {
			if (positiveOrNegative[subscripts[i]] < 0) {
				cnt++;
			}
		}
		return cnt;
	}

	/**
	 * 分類結果を出力する
	 * 
	 * @return
	 */
	public void outputClassify(String filePath, Properties prop) {
		int truePositive = 0;
		int trueNegative = 0;
		int falsePositive = 0;
		int falseNegative = 0;
		StringBuilder sb = new StringBuilder();
		sb.append(cases.length + "\t");
		for (int i = 0; i < cases.length; i++) {
			String[] sample = cases[i];
			int[] feature = features[i];
			int answer = positiveOrNegative[i];
			int result = classify[i];

			String sampleStr = Utils.join(sample, ",");
			String featureStr = Utils.joinInt(feature, ",");
			sb.append(sampleStr);
			sb.append("\t");
			sb.append(featureStr);
			sb.append("\t");
			sb.append(answer);
			sb.append("\t");
			sb.append(result);
			sb.append("\t");
			if (answer == 1) {
				if (result == 1) {
					sb.append(1);
					truePositive++;
				} else {
					sb.append(0);
					falseNegative++;
				}
			} else if (answer == -1) {
				if (result == -1) {
					sb.append(1);
					trueNegative++;
				} else {
					sb.append(0);
					falsePositive++;
				}
			}
			sb.append("\n");
		}

		TwoClassifyAnalyze classifyAnalyze = new TwoClassifyAnalyze();
		classifyAnalyze.setTruePositiveCnt(truePositive);
		classifyAnalyze.setTrueNegativeCnt(trueNegative);
		classifyAnalyze.setFalsePositiveCnt(falsePositive);
		classifyAnalyze.setFalseNegativeCnt(falseNegative);

		StringBuffer analyzeSb = new StringBuffer();
		analyzeSb.append(truePositive);
		analyzeSb.append("\t");
		analyzeSb.append(trueNegative);
		analyzeSb.append("\t");
		analyzeSb.append(falsePositive);
		analyzeSb.append("\t");
		analyzeSb.append(falseNegative);
		analyzeSb.append("\t");
		double wRecall = Double.parseDouble(prop.getProperty("randomforest.number.of.wrecall"));
		analyzeSb.append(classifyAnalyze.getFMeasure(wRecall));
		analyzeSb.append("\t");
		analyzeSb.append(classifyAnalyze.getRecall());
		analyzeSb.append("\t");
		analyzeSb.append(classifyAnalyze.getPrecision());
		analyzeSb.append("\n");

		Output.outputFile(filePath, analyzeSb);
		System.out.println(analyzeSb.toString());
		try {
			FileWriter fw = new FileWriter(new File(filePath), true);
			Output.outputFileWriter(fw, sb.toString());
			System.out.println(sb.toString());
		} catch (IOException e) {
			System.out.println("error: " + e.getMessage());
		}
	}

	/* getter and setter */

	public int getPositiveCnt() {
		return positiveCnt;
	}

	public void setPositiveCnt(int positiveCnt) {
		this.positiveCnt = positiveCnt;
	}

	public int getNegativeCnt() {
		return negativeCnt;
	}

	public void setNegativeCnt(int negativeCnt) {
		this.negativeCnt = negativeCnt;
	}

	public String[][] getCases() {
		return cases;
	}

	public void setCases(String[][] cases) {
		this.cases = cases;
	}

	public int[] getPositiveOrNegative() {
		return positiveOrNegative;
	}

	public void setPositiveOrNegative(int[] positiveOrNegative) {
		this.positiveOrNegative = positiveOrNegative;
	}

	public int getRandomforest_sampling_max() {
		return randomforest_sampling_max;
	}

	public int[] getClassify() {
		return classify;
	}

	public void setClassify(int[] classify) {
		this.classify = classify;
	}
}
