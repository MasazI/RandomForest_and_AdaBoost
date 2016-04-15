package jp.sprix.learning.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import jp.sprix.learning.ImageListData;

public class ReportAnalyze {
	/**
	 * データを解析する
	 * 
	 * @param data
	 * @return value
	 */
	public static void outputAnalyzeWholeFromFile(Properties prop, ReportData reportData,
			ImageListData imageListData) {
		// 質問するファイル名とカテゴリーの対応を取得
		HashMap<String, String> imageFileNameMap = imageListData.getImageFileNameMap();
		// 訓練済みカテゴリーのリスト
		ArrayList<String> trainCategoryList = reportData.getTrainCategoryList();

		// 正しく正解抽出した（+）のMap（keyがerrata行、valueはファイル名)
		HashMap<String[], String> truePositiveMap = reportData.getTruePositiveMap();
		// 誤って正解抽出した（-）のMap（keyがerrata行、valueはファイル名)
		HashMap<String[], String> falsePositiveMap = reportData.getFalsePositiveMap();

		// ファイル名Mapから質問ファイル名の配列を取得
		Set<String> keys = imageFileNameMap.keySet();
		// trueNegativeの最大数で初期化。質問画像数(keys)
		int trueNegativeCnt = reportData.getTrainDataCnt() * keys.size();
		int falseNegativeCnt = 0;
		int falsePositiveCnt = 0;

		// 出力ファイルの準備
		String outputFilePath = prop.getProperty("report.file");
		File errataFile = new File(prop.getProperty("errataFilePath"));
		File outputFile = new File(outputFilePath + "/report_" + errataFile.getName());
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 質問ファイル名でループ（質問の回数分ループする）
		int excludeCnt = 0;
		int includeCnt = 0;
		for (String key : keys) {
			StringBuilder sbLine = new StringBuilder();
			sbLine.append(key);
			// このファイル名の正解カテゴリーが訓練済みカテゴリーになければ飛ばす
			String category = imageFileNameMap.get(key);
			sbLine.append("\t");
			sbLine.append(category);
			sbLine.append("\t");
			if (!trainCategoryList.contains(category)) {
				sbLine.append("exclude");
				excludeCnt++;
				continue;
			} else {
				sbLine.append("include");
				includeCnt++;
			}
			sbLine.append("\t");

			// 正しいPositiveのマップに含まれていない場合(正解を抽出できていない)
			if (!truePositiveMap.containsValue(key)) {
				// ただ一つの正解カテゴリーを含んでいないので、間違ってnegativeと判断した回数を1インクリメント
				falseNegativeCnt++;
				sbLine.append("falseNegative");
				// 間違って正解として抽出したMap(正解以外の戻り値カテゴリー)に含まれる場合
				// （falsePositiveMapは、errata行がkey、質問ファイル名がvalue）
				// まず質問ファイル名がfalsePositiveMapに含まれているか確認する
				if (falsePositiveMap.containsValue(key)) {
					// 間違って正解として抽出したMapのキー（errata行）を取得しループ
					Set<String[]> falsePositiveKeys = falsePositiveMap.keySet();
					for (String[] falsePositiveKey : falsePositiveKeys) {
						// errata行（falsePositiveKey）でループし、間違った抽出回数分インクリメントする
						String falsePositiveFileName = falsePositiveMap.get(falsePositiveKey);
						if (falsePositiveFileName.equals(key)) {
							falsePositiveCnt++;
						}
					}
				}
				// 正しくNegativeに分類されたカウントを、間違って正解として抽出したカウントを引く
				trueNegativeCnt = trueNegativeCnt - falsePositiveCnt;
			}
			System.out.println(sbLine);
			printWriter.append(sbLine);
			printWriter.append("\n");
		}

		reportData.setFalseNegative(falseNegativeCnt);
		reportData.setTrueNegative(trueNegativeCnt);

		// 計算して出力
		int true_positive = reportData.getTruePositiveCnt();
		int false_positive = reportData.getFalsePositiveCnt();
		int true_negative = reportData.getTrueNegative();
		int false_negative = reportData.getFalseNegative();
		printWriter.print(true_positive);
		printWriter.print("\t" + true_negative);
		printWriter.print("\t" + false_positive);
		printWriter.print("\t" + false_negative);
		System.out.println("true_positive\t" + true_positive);
		System.out.println("false_positive\t" + false_positive);
		System.out.println("true_negative\t" + true_negative);
		System.out.println("false_negative\t" + false_negative);

		double precision = reportData.getPositivePrecision();
		double recall = reportData.getPositiveRecall();
		double error = reportData.getPositiveError();
		printWriter.print("\t" + recall);
		printWriter.print("\t" + precision);
		printWriter.print("\t" + error);
		printWriter.print("\t" + includeCnt);
		printWriter.println("\t" + excludeCnt);
		System.out.println("recall\t" + recall);
		System.out.println("precision\t" + precision);
		System.out.println("error\t" + error);
		System.out.println("include\t" + includeCnt);
		System.out.println("exclude\t" + excludeCnt);

		try {
			printWriter.close();
		} catch (Exception e) {
			System.out.println("[warning] can not close print writer. " + e.getMessage());
		}
	}

	/**
	 * データから解析データを返す
	 * 
	 * データからreportDataを作成して、レポートの値を返す
	 * 
	 * @param prop
	 *            プロパティ
	 * @param reportData
	 *            errataのデータ
	 * @param imageListData
	 *            質問リストデータ
	 * @return
	 */
	public static ReportData analyzeWholeFromData(Properties prop, ReportData reportData,
			ImageListData imageListData) {
		/* 解析する範囲のリスト読み込み */
		// 質問するファイル名とカテゴリーの対応を取得
		HashMap<String, String> imageFileNameMap = imageListData.getImageFileNameMap();
		// 訓練済みカテゴリーのリスト
		ArrayList<String> trainCategoryList = reportData.getTrainCategoryList();

		/* errataデータから取得出来る解析値 */
		// 正しく正解抽出した（+）のMap（keyがerrata行、valueはファイル名)
		HashMap<String[], String> truePositiveMap = reportData.getTruePositiveMap();
		// 誤って正解抽出した（-）のMap（keyがerrata行、valueはファイル名)
		HashMap<String[], String> falsePositiveMap = reportData.getFalsePositiveMap();

		/* 解析値を出す元の数値 */
		// ファイル名Mapから質問ファイル名の配列を取得
		Set<String> keys = imageFileNameMap.keySet();
		// trueNegativeの最大数で初期化。質問画像数(keys)×カテゴリー数。
		int trueNegativeCnt = reportData.getTrainDataCnt() * keys.size();
		int falseNegativeCnt = 0;
		int falsePositiveCnt = 0;
		// 正解が一つもない場合
		if (truePositiveMap == null) {
			reportData.setFalseNegative(keys.size());
			reportData.setTrueNegative(trueNegativeCnt);
			return reportData;
		}

		for (String key : keys) {
			StringBuilder sbLine = new StringBuilder();
			sbLine.append(key);
			// このファイル名の正解カテゴリーが訓練済みカテゴリーになければ飛ばす
			String category = imageFileNameMap.get(key);
			sbLine.append("\t");
			sbLine.append(category);
			sbLine.append("\t");
			if (!trainCategoryList.contains(category)) {
				sbLine.append("exclude");
				continue;
			} else {
				sbLine.append("include");
			}
			sbLine.append("\t");

			// 正しいPositiveのマップに含まれていない場合(正解を抽出できていない)
			if (!truePositiveMap.containsValue(key)) {
				// ただ一つの正解カテゴリーを含んでいないので、間違ってnegativeと判断した回数を1インクリメント
				falseNegativeCnt++;
				sbLine.append("falseNegative");
				// 間違って正解として抽出したMap(正解以外の戻り値カテゴリー)に含まれる場合
				// （falsePositiveMapは、errata行がkey、質問ファイル名がvalue）
				// まず質問ファイル名がfalsePositiveMapに含まれているか確認する
				if (falsePositiveMap.containsValue(key)) {
					// 間違って正解として抽出したMapのキー（errata行）を取得しループ
					Set<String[]> falsePositiveKeys = falsePositiveMap.keySet();
					for (String[] falsePositiveKey : falsePositiveKeys) {
						// errata行（falsePositiveKey）でループし、間違った抽出回数分インクリメントする
						String falsePositiveFileName = falsePositiveMap.get(falsePositiveKey);
						if (falsePositiveFileName.equals(key)) {
							falsePositiveCnt++;
						}
					}
				}
				// 正しくNegativeに分類されたカウントを、間違って正解として抽出したカウントを引く
				trueNegativeCnt = trueNegativeCnt - falsePositiveCnt;
			}

			// Debug用
			// System.out.println(sbLine);
		}

		reportData.setFalseNegative(falseNegativeCnt);
		reportData.setTrueNegative(trueNegativeCnt);

		return reportData;
	}
}
