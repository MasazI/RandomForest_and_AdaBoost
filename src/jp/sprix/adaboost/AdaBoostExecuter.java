package jp.sprix.adaboost;

import java.util.ArrayList;
import java.util.Properties;

import jp.sprix.component.ComponentErrataData;
import jp.sprix.component.ComponentPointDoubleData;
import jp.sprix.gui.ProtWindow;
import jp.sprix.io.Import;
import jp.sprix.io.Utils;
import jp.sprix.learning.data.TwoClassifyAnalyze;

/**
 * AdaBoostを実行する
 * 
 * @author root
 * 
 */
public class AdaBoostExecuter {
	/**
	 * errata adaboost
	 * 
	 * @param errataLines
	 */
	public static void adaBoostErrata(String errataFilePath) {
		// Properties
		Properties mProp = Import.importProperty();

		ArrayList<String[]> errataLines = Import.getArrayListFromFile(errataFilePath);
		// 全サンプルの入れ物
		ComponentErrataData componentErrataData = new ComponentErrataData(errataLines);

		// CaseDataの作成
		TrainCaseErrataData trainCaseErrataData = new TrainCaseErrataData(componentErrataData);

		// 識別器の作成
		AdaBoost adaBoost = new AdaBoost();
		FinalClassifier finalClassifier = adaBoost.makeClassifier(trainCaseErrataData,
				componentErrataData, mProp);

		// 識別実行
		int[] finalTrainClassify = adaBoost.getFinalClassifyValue(finalClassifier,
				trainCaseErrataData, componentErrataData, mProp);

		TwoClassifyAnalyze twoClassifyPointData = new TwoClassifyAnalyze(trainCaseErrataData,
				finalTrainClassify);
		System.out.println("訓練精度");
		System.out.print("Samples: " + componentErrataData.getSampleNumber() + "\tPositive: "
				+ componentErrataData.getSamplePositive() + "\tNegative: "
				+ componentErrataData.getSampleNegative());
		System.out.print("\tFMeasure: "
				+ twoClassifyPointData.getFMeasure(Double.parseDouble(Import.importProperty()
						.getProperty("number.of.wrecall"))) + "\tRecall: "
				+ twoClassifyPointData.getRecall() + "\tPRecision: "
				+ twoClassifyPointData.getPrecision());
		System.out.print("\tTruePositive: " + twoClassifyPointData.getTruePositiveCnt());
		System.out.print("\tTrueNegative: " + twoClassifyPointData.getTrueNegativeCnt());
		System.out.print("\tFalsePositive: " + twoClassifyPointData.getFalsePositiveCnt());
		System.out.println("\tFalseNegative: " + twoClassifyPointData.getFalseNegativeCnt());

		// debug用breakpoint
		// System.out.println("debug");
	}

	/**
	 * 汎化精度を出す
	 * 
	 * @param String
	 *            汎化精度に使用するerrataファイル
	 * @param String
	 *            しきい値辞書ファイル
	 * 
	 */
	public static void adaBoostErrataQuery(String errataQueryFilePath, String dictionary) {
		// Properties
		Properties mProp = Import.importProperty();

		// 辞書ファイルを読み込む
		ArrayList<String[]> dictionaryList = Import.getArrayListFromFile(dictionary);

		// インポート形式に変換
		double[] linearCoefficients = new double[dictionaryList.size()];
		String[] thresholdStrArray = new String[dictionaryList.size()];
		for (int i = 0; i < dictionaryList.size(); i++) {
			String[] thresholds = dictionaryList.get(i);
			double linerCoefficient = Double.parseDouble(thresholds[thresholds.length - 1]);
			linearCoefficients[i] = linerCoefficient;

			// 末尾を除いて結合
			StringBuffer buf = new StringBuffer();
			for (int j = 0; j < thresholds.length - 1; j++) {
				if (buf.length() > 0) {
					buf.append("\t");
				}
				buf.append(thresholds[j]);
			}
			thresholdStrArray[i] = buf.toString();
		}

		// フィルターを作成
		FinalClassifier finalClassifier = new FinalClassifier();
		finalClassifier.setLinearCoefficients(linearCoefficients);
		finalClassifier.setThresholdStrArray(thresholdStrArray);

		// 汎化精度用のコンポーネントと事例クラスを作成する
		AdaBoost adaBoost = new AdaBoost();
		// String queryFilePath = mProp.getProperty("adaboost.query");
		ArrayList<String[]> errataLinesQuery = Import.getArrayListFromFile(errataQueryFilePath);
		ComponentErrataData componentErrataDataQuery = new ComponentErrataData(errataLinesQuery);
		QueryCaseErrataData queryCaseErrataData = new QueryCaseErrataData(componentErrataDataQuery);
		// 汎化精度の測定
		int[] finalQueryClassify = adaBoost.getFinalClassifyValue(finalClassifier,
				queryCaseErrataData, componentErrataDataQuery, mProp);

		TwoClassifyAnalyze twoClassifyQueryPointData = new TwoClassifyAnalyze(queryCaseErrataData,
				finalQueryClassify);
		System.out.println("------------------------------------------------");
		System.out.println("汎化精度");
		System.out.print("Samples: " + componentErrataDataQuery.getSampleNumber() + "\tPositive: "
				+ componentErrataDataQuery.getSamplePositive() + "\tNegative: "
				+ componentErrataDataQuery.getSampleNegative());
		System.out.print("\tFMeasure: "
				+ twoClassifyQueryPointData.getFMeasure(Double.parseDouble(Import.importProperty()
						.getProperty("number.of.wrecall"))) + "\tRecall: "
				+ twoClassifyQueryPointData.getRecall() + "\tPRecision: "
				+ twoClassifyQueryPointData.getPrecision());
		System.out.print("\tTruePositive: " + twoClassifyQueryPointData.getTruePositiveCnt());
		System.out.print("\tTrueNegative: " + twoClassifyQueryPointData.getTrueNegativeCnt());
		System.out.print("\tFalsePositive: " + twoClassifyQueryPointData.getFalsePositiveCnt());
		System.out.println("\tFalseNegative: " + twoClassifyQueryPointData.getFalseNegativeCnt());

	}

	/**
	 * sample adaboost
	 */
	public static void adaBoostSample() {
		ComponentPointDoubleData componentPointData = new ComponentPointDoubleData("adaboost");
		TrainCasePointData trainCasePointData = new TrainCasePointData(componentPointData);

		// 事例の表示
		ProtWindow protWindow = new ProtWindow();
		protWindow.displaySample(componentPointData);

		// 識別器の作成
		AdaBoost adaBoost = new AdaBoost();
		FinalClassifier finalClassifier = adaBoost.makeClassifier(trainCasePointData,
				componentPointData);

		// 精度の測定
		int[] finalClassify = adaBoost.getFinalClassifyValue(finalClassifier, trainCasePointData,
				componentPointData);
		protWindow.displayClassify(trainCasePointData, finalClassify, componentPointData);

		// 数値データを出力
		TwoClassifyAnalyze twoClassifyPointData = new TwoClassifyAnalyze(trainCasePointData,
				finalClassify);
		System.out.println("FMeasure: "
				+ twoClassifyPointData.getFMeasure(Double.parseDouble(Import.importProperty()
						.getProperty("number.of.wrecall"))) + "\t"
				+ twoClassifyPointData.getRecall() + "\t" + twoClassifyPointData.getPrecision());

		// debug用breakpoint
		System.out.println("debug");
	}
}
