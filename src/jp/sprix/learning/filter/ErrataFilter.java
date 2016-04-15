package jp.sprix.learning.filter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import jp.sprix.cv.CVErrataGeometry;
import jp.sprix.io.Import;
import jp.sprix.io.Output;
import jp.sprix.io.Utils;
import jp.sprix.learning.ImageListData;
import jp.sprix.learning.report.ReportAnalyze;
import jp.sprix.learning.report.ReportData;
import jp.sprix.threshold.CoordinatesThreshold;

public class ErrataFilter {
	/**
	 * フィルターを実行し、全体に対するレポートを出力する
	 */
	public static void filterCoordinateLearning(String errataFilePath, String learningType) {
		// プロパティの読み込み
		Properties mProp = Import.importProperty();
		mProp.setProperty("learningType", learningType);

		// レポートオブジェクト
		ReportData reportData = new ReportData();
		reportData.initialize(mProp);
		// 質問画像のリストオブジェクト
		ImageListData imageListData = new ImageListData();
		imageListData.initialize(mProp);

		// 初回errataファイルを読み込みリストに入れる
		ArrayList<String[]> errataLines = Import.getArrayListFromFile(errataFilePath);

		// 閾値のオブジェクト
		CoordinatesThreshold threshold = new CoordinatesThreshold(mProp);
		// ファイル出力用オブジェクト
		StringBuilder sb = new StringBuilder();

		// 内角
		/*
		 * while (!threshold.isAngleFinish()) { setReportData(prop, errataLines,
		 * threshold, reportData, imageListData);
		 * Output.outputReportDataLine("angle", threshold, reportData, sb); //
		 * 次へ threshold.nextAngle(); } threshold.resetAngle();
		 * 
		 * // 周囲長Max while (!threshold.isPerimeterMaxFinish()) {
		 * setReportData(prop, errataLines, threshold, reportData,
		 * imageListData); Output.outputReportDataLine("perimetermax",
		 * threshold, reportData, sb); // 次へ threshold.nextPerimeterMax(); }
		 * threshold.resetPerimeterMax();
		 * 
		 * // 周囲長Min while (!threshold.isPerimeterMinFinish()) {
		 * setReportData(prop, errataLines, threshold, reportData,
		 * imageListData); Output.outputReportDataLine("perimetermin",
		 * threshold, reportData, sb); // 次へ threshold.nextPerimeterMin(); }
		 * threshold.resetPerimeterMin();
		 */
		// ファイル出力
		Output.outputFile(mProp.getProperty("report.file.path") + "/filter_"
				+ new File(errataFilePath).getName(), sb);
	}

	/**
	 * 指定した閾値でerrataデータをフィルターし、結果をreportDataオブジェクトに設定する。
	 * 実行する度に、reportDataは初期化する。
	 * 
	 * @param prop
	 *            プロパティ
	 * @param errataLines
	 *            errataデータ
	 * @param threshold
	 *            閾値
	 * @param reportData
	 *            解析結果のレポートデータ
	 * @param imageListData
	 *            質問画像のリストデータをもつオブジェクト
	 */
	public static void setReportData(Properties prop, ArrayList<String[]> errataLines,
			CoordinatesThreshold threshold, ReportData reportData, ImageListData imageListData) {
		// filterを通った結果を保存するMap. key:質問ファイル名, value:errata行
		HashMap<String[], String> filterResult = new HashMap<String[], String>();
		// 閾値オブジェクト
		for (String[] lineSplitTab : errataLines) {
			boolean result = lineFilter(prop, threshold, lineSplitTab);
			if (result) {
				filterResult.put(lineSplitTab, lineSplitTab[0]);
			}
		}
		// フィルターの結果からオブジェクトを作成
		reportData.loadDataFromMap(true, filterResult);
		filterResult = null;

		// レポートデータの作成
		ReportAnalyze.analyzeWholeFromData(prop, reportData, imageListData);
	}

	/**
	 * 結果行毎にフィルターをかけた結果を返す
	 * 
	 * @param prop
	 *            プロパティー
	 * @param threshold
	 *            閾値オブジェクト
	 * @param lineSplitTab
	 *            一行をタブ区切りにした文字列配列
	 * @return true：閾値に通る、false：閾値に通らない
	 */
	public static boolean lineFilter(Properties prop, CoordinatesThreshold threshold,
			String[] lineSplitTab) {
		int scale = Integer.parseInt((String) prop.get("train.query.scale"));

		// ファイル名を出力
		// System.out.println("[filter]" + lineSplitTab[0]);

		// 座標情報がエラーの場合
		if (lineSplitTab[3].equals("NaN") || lineSplitTab[4].equals("NaN")
				|| lineSplitTab[5].equals("NaN") || lineSplitTab[6].equals("NaN")
				|| lineSplitTab[7].equals("NaN") || lineSplitTab[8].equals("NaN")
				|| lineSplitTab[9].equals("NaN") || lineSplitTab[10].equals("NaN")) {
			return false;
		}

		// 座標情報を切り出す;
		double[] coordinate1 = { Double.parseDouble(lineSplitTab[3]) / scale,
				Double.parseDouble(lineSplitTab[4]) / scale };
		double[] coordinate2 = { Double.parseDouble(lineSplitTab[5]) / scale,
				Double.parseDouble(lineSplitTab[6]) / scale };
		double[] coordinate3 = { Double.parseDouble(lineSplitTab[7]) / scale,
				Double.parseDouble(lineSplitTab[8]) / scale };
		double[] coordinate4 = { Double.parseDouble(lineSplitTab[9]) / scale,
				Double.parseDouble(lineSplitTab[10]) / scale };
		ArrayList<double[]> coordinates = new ArrayList<double[]>();
		coordinates.add(coordinate1);
		coordinates.add(coordinate2);
		coordinates.add(coordinate3);
		coordinates.add(coordinate4);

		return doFilter(coordinates, threshold, lineSplitTab);
	}

	/**
	 * 座標のフィルターにかける
	 * 
	 * @param coordinates
	 * @return
	 */
	public static boolean doFilter(ArrayList<double[]> coordinates, CoordinatesThreshold threshold,
			String[] lineSplitTab) {
		// 内角
		double angle0 = CVErrataGeometry.getInteriorAngle(coordinates, 0);
		double angle1 = CVErrataGeometry.getInteriorAngle(coordinates, 1);
		double angle2 = CVErrataGeometry.getInteriorAngle(coordinates, 2);
		double angle3 = CVErrataGeometry.getInteriorAngle(coordinates, 3);

		// 周囲
		double perimeter = CVErrataGeometry.getPerimeter(coordinates);

		// 内積
		double inner0 = CVErrataGeometry.getInner(coordinates, 0);
		double inner1 = CVErrataGeometry.getInner(coordinates, 1);

		CoordinateFilterExecuter executer = new CoordinateFilterExecuter(threshold);

		/*
		 * if (!executer.executeInteriorAngle(angle0)) { //
		 * System.out.println("[FilterToFalse]angle0" + lineSplitTab[0] + // ":"
		 * + lineSplitTab[1]); return false; } if
		 * (!executer.executeInteriorAngle(angle1)) { //
		 * System.out.println("[FilterToFalse]angle1" + lineSplitTab[0] + // ":"
		 * + lineSplitTab[1]); return false; } if
		 * (!executer.executeInteriorAngle(angle2)) { //
		 * System.out.println("[FilterToFalse]angle2" + lineSplitTab[0] + // ":"
		 * + lineSplitTab[1]); return false; } if
		 * (!executer.executeInteriorAngle(angle3)) { //
		 * System.out.println("[FilterToFalse]angle3" + lineSplitTab[0] + // ":"
		 * + lineSplitTab[1]); return false; } if
		 * (!executer.executePerimeter(perimeter)) { // System.out //
		 * .println("[FilterToFalse]perimeter" + lineSplitTab[0] + ":" + //
		 * lineSplitTab[1]); return false; } if (!executer.executeInner(inner0))
		 * { // System.out.println("[FilterToFalse]inner0" + lineSplitTab[0] +
		 * // ":" + lineSplitTab[1]); return false; } if
		 * (!executer.executeInner(inner1)) { //
		 * System.out.println("[FilterToFalse]inner1" + lineSplitTab[0] + // ":"
		 * + lineSplitTab[1]); return false; }
		 */

		return true;
	}

	/**
	 * errataファイルの各行を座標のフィルタにかけ、新しいerrataファイルを出力する
	 * 
	 * @param errataFilePath
	 * @param angle
	 * @param perimetermax
	 * @param perimetermin
	 */
	public static void filterCoordinate(String errataFilePath, String angle, String perimetermax,
			String perimetermin) {
		Properties mProp = Import.importProperty();
		// 閾値オブジェクト
		CoordinatesThreshold threshold = new CoordinatesThreshold(mProp);
		/*
		 * threshold.setAngleMinThreshold(Double.parseDouble(angle));
		 * threshold.setAngleMaxThreshold(180 - Double.parseDouble(angle));
		 * threshold.setPerimeterMaxThreshold(Double.parseDouble(perimetermax));
		 * threshold.setPerimeterMinThreshold(Double.parseDouble(perimetermin));
		 */

		// 出力ファイルの準備
		PrintWriter printWriter = null;

		String outputFilePath = errataFilePath + "_cf";
		File outputFile = new File(outputFilePath);
		try {
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
		} catch (IOException e) {
			System.out.println("[error] _cf file. " + e.getMessage());
		}

		// errataファイルを読み込む
		ArrayList<String[]> errataArray = Import.getArrayListFromFile(errataFilePath);
		try {
			for (String[] lineSplitTab : errataArray) {
				boolean result = lineFilter(mProp, threshold, lineSplitTab);
				String line = Utils.join(lineSplitTab, "\t");
				if (result) {
					printWriter.println(line);
					System.out.println(line + "\tinclude");
				} else {
					System.out.println(line + "\texclude");
				}
			}
			// 出力ファイルクローズ
			printWriter.close();
		} catch (Exception e) {
			System.out.println("[error] can not load file. " + errataFilePath);
			return;
		}
	}

}
