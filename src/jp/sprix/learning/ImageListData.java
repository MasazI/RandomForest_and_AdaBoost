package jp.sprix.learning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * train&Query image data
 * 
 * @author root
 * 
 */
public class ImageListData {
	private static final String DATA_FILE_MARK = "[DATA]";
	// private static final String IMAGE_LIST_TRAIN_PATH =
	// "imagelist.train.path";
	private static final String IMAGE_LIST_QUERY_ALL_PATH = "imagelist.all.path";

	/**
	 * imageNameMap ポストするファイル名とNoをひもづける。Noはレスポンスファイル名にも使用し No.xml で保存する。
	 * 
	 * <fileName, fileNo>
	 */
	private HashMap<String, String> imageFilePathMap;

	/**
	 * imageNameMap ポストするファイル名とカテゴリーを結びつける。
	 * 
	 * <fileName, category>
	 */
	private HashMap<String, String> imageFileNameMap;

	/**
	 * answerMap ファイルのNoとカテゴリーを対応付けるファイル
	 * 
	 * <No, category>
	 */
	private HashMap<String, String> answerMap;

	// 正解抽出個数の文字列
	private String candidates = null;

	/**
	 * Constracter
	 */
	public ImageListData() {
		imageFilePathMap = new HashMap<String, String>();
		imageFileNameMap = new HashMap<String, String>();
		answerMap = new HashMap<String, String>();
	}

	/**
	 * input data from file at filepath
	 * 
	 * @param learningType
	 * @return
	 */
	public void initialize(Properties prop) {
		candidates = (String) prop.getProperty("number.of.candidates");
		// String learningType = (String) prop.get("learningType");

		// プロパティをロードする
		String imageListPathAll = prop.getProperty(IMAGE_LIST_QUERY_ALL_PATH);
		loaddata(imageListPathAll);

	}

	/**
	 * load data
	 * 
	 * @param filename
	 * @return
	 */
	public void loaddata(String filename) {
		File file = new File(filename);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.equals(DATA_FILE_MARK)) {
					continue;
				}
				// split
				String[] data = line.split("\t");
				imageFilePathMap.put(data[1], data[0]);
				imageFileNameMap.put(data[2], data[3]);
				answerMap.put(data[0], data[2]);
			}
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// getter

	public HashMap<String, String> getImageFilePathMap() {
		return imageFilePathMap;
	}

	public HashMap<String, String> getImageFileNameMap() {
		return imageFileNameMap;
	}

	public HashMap<String, String> getAnswerMap() {
		return answerMap;
	}

	public String getCandidates() {
		return candidates;
	}
}
