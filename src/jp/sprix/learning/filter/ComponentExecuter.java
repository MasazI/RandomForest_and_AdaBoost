package jp.sprix.learning.filter;

import java.util.ArrayList;
import java.util.Properties;

import jp.sprix.component.ComponentErrataData;
import jp.sprix.component.ComponentFilterResult;
import jp.sprix.io.Import;
import jp.sprix.io.Output;
import jp.sprix.learning.data.TwoClassifyErrataData;
import jp.sprix.threshold.CoordinatesThreshold;

/**
 * 各フィルター毎に実行・評価するためのクラス
 * 
 * @author root
 * 
 */
public class ComponentExecuter {
	/**
	 * errataファイルからフィルターを実行する
	 */
	public static void componentFromErrataExec(String errataFilePath) {
		ArrayList<String[]> errataLines = Import.getArrayListFromFile(errataFilePath);
		if (errataLines == null) {
			return;
		}
		componentExec(errataLines);

	}

	/**
	 * errataデータからフィルターを実行する
	 */
	public static void componentExec(ArrayList<String[]> errataLines) {
		// errataデータから母集団を作成
		ComponentErrataData componentData = new ComponentErrataData(errataLines);
		// 全体数を出力
		System.out.println("AllsampleNum\t" + componentData.getSampleNumber());
		System.out.println("AllpositiveNum\t" + componentData.getSamplePositive());
		System.out.println("AllnegativeNum\t" + componentData.getSampleNegative());

		// フィルター結果オブジェクト
		TwoClassifyErrataData twoClassifyData = new TwoClassifyErrataData();

		// プロパティ
		Properties mProp = Import.importProperty();

		// フィルターにかけて、結果オブジェクトに格納
		CoordinatesThreshold threshold = new CoordinatesThreshold(mProp);

		// F値算出用Recallの重み
		twoClassifyData.setWeightRecall(Double.parseDouble(Import.importProperty().getProperty(
				"number.of.wrecall")));
		// ファイル出力用バッファ
		StringBuilder sb = new StringBuilder();

		// 内角
		ComponentFilterResult angleMinfilterResult = filterExec("angleMin", threshold, errataLines,
				twoClassifyData, sb);
		outputDebug(angleMinfilterResult, sb);
		twoClassifyData.reset();

		// 周囲長MIN
		ComponentFilterResult perimeterMinFilterResult = filterExec("perimeterMin",
				(CoordinatesThreshold) angleMinfilterResult.getThreshold(), errataLines,
				twoClassifyData, sb);
		outputDebug(angleMinfilterResult, sb);
		twoClassifyData.reset();

		// 周囲長MAX
		ComponentFilterResult perimeterMaxFilterResult = filterExec("perimeterMax",
				(CoordinatesThreshold) perimeterMinFilterResult.getThreshold(), errataLines,
				twoClassifyData, sb);
		outputDebug(angleMinfilterResult, sb);
		twoClassifyData.reset();

		// ファイルに出力
		Output.outputFile(Import.importProperty().getProperty("report.component.path"), sb);

		// 最終出力
		TwoClassifyErrataData conclusiveTwoClassifyData = filterExecAtThreshold(threshold,
				errataLines);
		conclusiveTwoClassifyData.getConclusiveAnswer(componentData);
	}

	/**
	 * 数値アウトプット
	 * 
	 * @param result
	 * @param sb
	 */
	public static void outputDebug(ComponentFilterResult result, StringBuilder sb) {
		System.out.println("Result angleMin" + "\t" + result.getFMeasure() + "\t"
				+ result.getRecall() + "\t" + result.getPrecision() + "\t"
				+ ((CoordinatesThreshold) result.getThreshold()).output());
		sb.append("Result angleMin" + "\t" + result.getFMeasure() + "\t" + result.getRecall()
				+ "\t" + result.getPrecision() + "\t"
				+ ((CoordinatesThreshold) result.getThreshold()).output());
		sb.append("\n");
	}

	/**
	 * 指定されたタイプのフィルターを順番に実行し、一番F値が高い結果を返す
	 * 
	 * @param type
	 *            angleMin or angleMax or perimeterMin or perimeterMax
	 * @param threshold
	 * @param errataLines
	 * @param twoClassifyData
	 * @param StringBuild
	 *            ファイル出力用
	 */
	public static ComponentFilterResult filterExec(String type, CoordinatesThreshold threshold,
			ArrayList<String[]> errataLines, TwoClassifyErrataData twoClassifyData, StringBuilder sb) {

		boolean isFinish = false;
		double maxFValue = 0;
		double recall = 0;
		double precision = 0;
		String maxThresholdStr = null;
		while (!isFinish) {
			// リセット
			twoClassifyData.reset();
			// ある閾値でのフィルター結果
			for (String[] lineSplitTab : errataLines) {
				Boolean result = ErrataFilter.lineFilter(Import.importProperty(), threshold,
						lineSplitTab);
				if (result) {
					twoClassifyData.addPositiveSampleList(lineSplitTab,
							lineSplitTab[lineSplitTab.length - 1]);
				} else {
					twoClassifyData.addNegativeSampleList(lineSplitTab,
							lineSplitTab[lineSplitTab.length - 1]);
				}
			}
			double fValue = twoClassifyData.getFMeasure(twoClassifyData.getWeightRecall());

			// type
			System.out.print(type);
			System.out.print("\t");
			// 閾値を出力
			System.out.print(threshold.output());
			System.out.print("\t");
			// test値を出力
			System.out.print(twoClassifyData.getTruePositiveSampleListCnt());
			System.out.print("\t");
			System.out.print(twoClassifyData.getTrueNegativeSampleListCnt());
			System.out.print("\t");
			System.out.print(twoClassifyData.getFalsePositiveSampleListCnt());
			System.out.print("\t");
			System.out.print(twoClassifyData.getFalseNegativeSampleListCnt());
			System.out.print("\t");
			System.out.print(twoClassifyData.getRecall());
			System.out.print("\t");
			System.out.print(twoClassifyData.getPrecision());
			System.out.print("\t");
			System.out.print(fValue);
			System.out.print(threshold.output());
			System.out.println("\t");
			// test値をファイルに出力
			// type
			sb.append(type);
			sb.append("\t");
			sb.append(threshold.output());
			sb.append("\t");
			sb.append(twoClassifyData.getTruePositiveSampleListCnt());
			sb.append("\t");
			sb.append(twoClassifyData.getTrueNegativeSampleListCnt());
			sb.append("\t");
			sb.append(twoClassifyData.getFalsePositiveSampleListCnt());
			sb.append("\t");
			sb.append(twoClassifyData.getFalseNegativeSampleListCnt());
			sb.append("\t");
			sb.append(twoClassifyData.getRecall());
			sb.append("\t");
			sb.append(twoClassifyData.getPrecision());
			sb.append("\t");
			sb.append(fValue);
			sb.append("\n");
			// fValueの最大値を更新
			if (maxFValue < fValue) {
				maxFValue = fValue;
				maxThresholdStr = threshold.output();
				recall = twoClassifyData.getRecall();
				precision = twoClassifyData.getPrecision();
			}

			if (type.equals("angleMin")) {
				threshold.nextAngle();
				// isFinish = threshold.isAngleFinish();
			} else if (type.equals("angleMax")) {
				threshold.nextAngle();
				// isFinish = threshold.isAngleFinish();
			} else if (type.equals("perimeterMin")) {
				// threshold.nextPerimeterMin();
				// isFinish = threshold.isPerimeterMinFinish();
			} else if (type.equals("perimeterMax")) {
				// threshold.nextPerimeterMax();
				// isFinish = threshold.isPerimeterMaxFinish();
			}
		}

		threshold.importThreshold(maxThresholdStr);
		ComponentFilterResult filterResult = new ComponentFilterResult(maxFValue, threshold);
		filterResult.setRecall(recall);
		filterResult.setPrecision(precision);

		return filterResult;
	}

	/**
	 * 指定した閾値で分類を実行して、戻り値として返す
	 * 
	 * @param type
	 * @param threshold
	 * @param errataLines
	 * @param twoClassifyData
	 * @param sb
	 * @return
	 */
	public static TwoClassifyErrataData filterExecAtThreshold(CoordinatesThreshold threshold,
			ArrayList<String[]> errataLines) {
		TwoClassifyErrataData twoClassifyData = new TwoClassifyErrataData();
		for (String[] lineSplitTab : errataLines) {
			Boolean result = ErrataFilter.lineFilter(Import.importProperty(), threshold,
					lineSplitTab);
			if (result) {
				twoClassifyData.addPositiveSampleList(lineSplitTab,
						lineSplitTab[lineSplitTab.length - 1]);
			} else {
				twoClassifyData.addNegativeSampleList(lineSplitTab,
						lineSplitTab[lineSplitTab.length - 1]);
			}
		}
		return twoClassifyData;
	}
}
