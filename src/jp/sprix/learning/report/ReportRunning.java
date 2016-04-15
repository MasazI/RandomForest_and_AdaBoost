package jp.sprix.learning.report;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import jp.sprix.learning.ImageListData;

public class ReportRunning {
	// property
	private static final String PROPERTIES = "data.properties";

	// report
	public ReportData reportData = new ReportData();

	public ImageListData imageListData = new ImageListData();

	/**
	 * 
	 * @param importFilePath
	 *            レポートを出力するレポートファイル
	 * @param learningType
	 */
	public void outputReport(String importFilePath, String learningType) {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(PROPERTIES));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		prop.setProperty("learningType", learningType);
		prop.setProperty("errataFilePath", importFilePath);

		reportData.initialize(prop);

		// errataファイルをインポートする
		importFile(importFilePath);

		// image list data setting
		imageListData.initialize(prop);

		// analyze
		ReportAnalyze.outputAnalyzeWholeFromFile(prop, reportData, imageListData);
	}

	/**
	 * errataファイルをインポートする。
	 * 
	 * 「+」の行はtruePositive（正しく正解として抽出したパターン）に1加算する
	 * 「-」の行はfalsePositive（間違って正解として抽出したパターン）に1加算する
	 * 
	 * @param importFilePath
	 *            errata file
	 */
	private void importFile(String importFilePath) {
		try {
			FileReader reader = new FileReader(importFilePath);
			BufferedReader buffer = new BufferedReader(reader);
			String line = null;
			while ((line = buffer.readLine()) != null) {
				String[] lineSplitTab = line.split("\t");
				int flag = lineSplitTab.length - 1;
				String flagStr = lineSplitTab[flag];
				if (flagStr.equals("+")) {
					// タブ区切りの行がkeyで、質問ファイル名valueのMapを作成
					reportData.setTruePositiveMap(lineSplitTab[0], lineSplitTab);
				} else if (flagStr.equals("-")) {
					// タブ区切りの行がkeyで、質問ファイル名がvalueのMapを作成
					reportData.setFalsePositiveMap(lineSplitTab[0], lineSplitTab);
				}
			}
			buffer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
}
