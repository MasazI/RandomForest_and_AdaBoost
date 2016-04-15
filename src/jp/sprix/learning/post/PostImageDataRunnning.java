package jp.sprix.learning.post;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import jp.sprix.learning.ImageListData;
import jp.sprix.learning.evaluation.EvaluationKurusugawa;

public class PostImageDataRunnning {
	private static final String PROPERTIES = "data.properties";

	/**
	 * 質問のエンジンへのポストを開始
	 * 
	 * @param learningType
	 *            タイプを指定（allかpartかtrain。タイプによって、ポストするファイルを記述するファイルが異なる）
	 * @param int 何位までパースするか
	 */
	public void postImageDataStart(String learningType, int rank) {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(PROPERTIES));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		prop.setProperty("learningType", learningType);

		// ポストするデータのリストを作成する
		// 作成されるのは、質問ファイルとNoのMap、Noとカテゴリーのマップ
		ImageListData imageListData = new ImageListData();
		imageListData.initialize(prop);

		// イメージデータをポストし、レスポンスの結果ファイルを保存する
		try {
			PostForKurusugawa postForKurusugawa = new PostForKurusugawa(prop);
			postForKurusugawa.postImageData(imageListData);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// レスポンスデータを解析する
		// try {
		// //
		// EvaluationKurusugawa.evaluation(prop, responseFileMap, rank);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}
}
