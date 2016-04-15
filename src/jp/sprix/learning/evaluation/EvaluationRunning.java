package jp.sprix.learning.evaluation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * レスポンスデータの解析を行うクラス
 * 
 * @author root
 */
public class EvaluationRunning {
	// property
	private static final String PROPERTIES = "data.properties";

	/**
	 * 解析を行い、errataファイルを出力する
	 * 
	 * @param String
	 *            learningType
	 * @param int 何位までパースするか
	 */
	public void evaluation(String learningType, int rank) {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(PROPERTIES));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		prop.setProperty("learningType", learningType);

		try {
			// 作成したレスポンスファイルで解析を行う
			EvaluationKurusugawa.evaluation(prop, makeResponseFileMap(prop), rank);
		} catch (IOException e) {
			System.out.println("[error]");
		}
	}

	/**
	 * responseFileMapを作成する
	 * 
	 * keyはレスポンスファイルのパス。valueは{質問ファイル名, 正解カテゴリー}
	 * 
	 * @return
	 */
	public HashMap<String, String[]> makeResponseFileMap(Properties prop) {
		String imageListFilePath = null;
		String responseFilePath = null;
		String learningType = (String) prop.get("learningType");
		if (learningType.equals("train")) {
		} else if (learningType.equals("all")) {
			imageListFilePath = (String) prop.get("imagelist.all.path");
			//imageListFilePath = (String) prop.get("adaboost.train");
			responseFilePath = (String) prop.get("response.xml.path");
		} else if (learningType.equals("part")) {
		}

		HashMap<String, String[]> responseFileMap = new HashMap<String, String[]>();

		FileReader reader;
		try {
			reader = new FileReader(imageListFilePath);
		} catch (FileNotFoundException e) {
			System.out.println("[error] image list file does not exists.");
			return null;
		}
		BufferedReader buffer = new BufferedReader(reader);
		String line = null;
		try {
			while ((line = buffer.readLine()) != null) {
				String[] lineSplitTab = line.split("\t");
				// 質問ファイル名
				String questionFileName = lineSplitTab[2];
				// カテゴリー名
				String categoryName = lineSplitTab[3];
				// レスポンスファイルパスを作成
				String responseFile = responseFilePath + "/" + lineSplitTab[0];
				String[] value = { questionFileName, categoryName };

				// Mapに登録する
				responseFileMap.put(responseFile, value);
			}
		} catch (IOException e) {
			System.out.println("[error] image list file read exception. " + e.getMessage());
		}
		try {
			buffer.close();
		} catch (IOException e) {
			System.out.println("[error] can not close buffer. " + e.getMessage());
		}
		return responseFileMap;
	}
}
