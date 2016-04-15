package jp.sprix.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * データを読み込むクラス
 * 
 * @author root
 * 
 */
public class Import {
	/**
	 * プロパティファイル
	 */
	private static final String PROPERTIES = "data.properties";

	/**
	 * ファイルを読み込んで、各行のタブ区切る配列のリストを返す
	 * 
	 * @param filePath
	 * @return
	 */
	public static ArrayList<String[]> getArrayListFromFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("[error] can not load errata file. " + filePath);
			return null;
		}
		ArrayList<String[]> fileLines = new ArrayList<String[]>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] lineSplitTab = line.split("\t");
				fileLines.add(lineSplitTab);
			}
			// バッファのクローズ
			br.close();
		} catch (Exception e) {
			System.out.println("[error] errata file." + filePath + ". " + e.getMessage());
		}
		return fileLines;
	}

	/**
	 * プロパティを読み込む
	 * 
	 * @return
	 */
	public static Properties importProperty() {
		// プロパティの読み込み
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(PROPERTIES));
		} catch (FileNotFoundException e) {
			System.out.println("[error] properties. " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println("[error] properties. " + e.getMessage());
			return null;
		}
		return prop;
	}
}
