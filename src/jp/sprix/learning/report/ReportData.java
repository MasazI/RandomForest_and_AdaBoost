package jp.sprix.learning.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * 
 * @author root
 * 
 */
public class ReportData {
	private static final String TRAIN_CATEGORY_FILE_PATH = "all.train.category";

	/**
	 * true positive map
	 * 
	 * <errata line, fileName>
	 */
	private HashMap<String[], String> truePositiveMap = null;

	/**
	 * false positive map
	 * 
	 * <errata line, fileName>
	 */
	private HashMap<String[], String> falsePositiveMap = null;

	/**
	 * 正しく抽出しなかった個数
	 */
	private int trueNegative = 0;

	/**
	 * 誤って抽出しなかった個数
	 */
	private int falseNegative = 0;

	// 訓練済みカテゴリーのリスト
	private ArrayList<String> trainCategoryList = null;

	/**
	 * 初期化
	 * 
	 * @param prop
	 */
	public void initialize(Properties prop) {
		String trainCaterogyListPath = (String) prop.get(TRAIN_CATEGORY_FILE_PATH);
		setTrainCategoryList(trainCaterogyListPath);
	}

	/**
	 * Mapからデータを読み込み、正事例と負事例に分類する
	 * 
	 * @param boolean 初期化してからMapデータを読み込むかどうか
	 * @param resultMap
	 */
	public void loadDataFromMap(boolean init, HashMap<String[], String> resultMap) {
		if (init) {
			// 初期化
			truePositiveMap = null;
			falsePositiveMap = null;
			trueNegative = 0;
			falseNegative = 0;
		}

		for (Map.Entry<String[], String> e : resultMap.entrySet()) {
			String fileName = e.getValue();
			String[] lineSplitTab = e.getKey();
			// 最後に+か-を入れている
			int flag = lineSplitTab.length - 1;
			String flagStr = lineSplitTab[flag];
			if (flagStr.equals("+")) {
				setTruePositiveMap(fileName, lineSplitTab);
			} else if (flagStr.equals("-")) {
				setFalsePositiveMap(fileName, lineSplitTab);
			}
		}
	}

	/**
	 * 訓練隅カテゴリーを設定する
	 * 
	 * @param listFilePath
	 */
	private void setTrainCategoryList(String listFilePath) {
		File file = new File(listFilePath);
		trainCategoryList = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				trainCategoryList.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 訓練済みカテゴリーのリストを取得する
	 * 
	 * @return
	 */
	public ArrayList<String> getTrainCategoryList() {
		return trainCategoryList;
	}

	/**
	 * get train data cnt
	 * 
	 * @return all train Data Count
	 */
	public int getTrainDataCnt() {
		if (trainCategoryList == null) {
			return 0;
		}

		return trainCategoryList.size();
	}

	/**
	 * keyを一意にするため、truePositiveMapのkeyはerrata行にする
	 * 
	 * @param key
	 *            質問画像名
	 * @param data
	 *            errata行
	 */
	public void setTruePositiveMap(String key, String[] data) {
		if (truePositiveMap == null) {
			truePositiveMap = new HashMap<String[], String>();
		}
		truePositiveMap.put(data, key);
	}

	/**
	 * 
	 * @return
	 */
	public HashMap<String[], String> getTruePositiveMap() {
		return truePositiveMap;
	}

	/**
	 * 
	 * @return
	 */
	public int getTruePositiveCnt() {
		if (truePositiveMap == null) {
			return 0;
		}
		return truePositiveMap.size();
	}

	/**
	 * 
	 * @param key
	 * @param data
	 */
	public void setFalsePositiveMap(String key, String[] data) {
		if (falsePositiveMap == null) {
			falsePositiveMap = new HashMap<String[], String>();
		}
		falsePositiveMap.put(data, key);
	}

	/**
	 * 
	 * @return
	 */
	public HashMap<String[], String> getFalsePositiveMap() {
		return falsePositiveMap;
	}

	/**
	 * 
	 * @return
	 */
	public int getFalsePositiveCnt() {
		if (falsePositiveMap == null) {
			return 0;
		}
		return falsePositiveMap.size();
	}

	/**
	 * 
	 * @param trueNegativeCnt
	 */
	public void setTrueNegative(int trueNegativeCnt) {
		trueNegative = trueNegativeCnt;
	}

	/**
	 * 
	 * @param falseNegativeCnt
	 */
	public void setFalseNegative(int falseNegativeCnt) {
		falseNegative = falseNegativeCnt;
	}

	/**
	 * 
	 * @return
	 */
	public int getTrueNegative() {
		return trueNegative;
	}

	/**
	 * 
	 * @return
	 */
	public int getFalseNegative() {
		return falseNegative;
	}

	/**
	 * get positive precision
	 * 
	 * @return
	 */
	public double getPositivePrecision() {
		double tp = getTruePositiveCnt();
		double fp = getFalsePositiveCnt();

		// 演算不可能なら0を返す
		if ((tp + fp) == 0) {
			return 0;
		}
		return tp / (tp + fp);
	}

	/**
	 * get positive recall
	 * 
	 * @return
	 */
	public double getPositiveRecall() {
		double tp = getTruePositiveCnt();
		double fn = getFalseNegative();
		// recallが計算出来ない場合は0を返す
		if ((tp + fn) == 0) {
			return 0;
		}

		return tp / (tp + fn);
	}

	/**
	 * get positive error
	 * 
	 * @return
	 */
	public double getPositiveError() {
		double tp = getTruePositiveCnt();
		double tn = getTrueNegative();
		double fp = getFalsePositiveCnt();
		double fn = getFalseNegative();

		if ((tp + fp + fn + tn) == 0) {
			return 0;
		}

		return (fp + fn) / (tp + fp + fn + tn);
	}

	/**
	 * 現在の結果を文字列で返す
	 * 
	 * @return
	 */
	public String output() {
		StringBuilder sb = new StringBuilder();
		sb.append(getTruePositiveCnt());
		sb.append("\t");
		sb.append(getTrueNegative());
		sb.append("\t");
		sb.append(getFalsePositiveCnt());
		sb.append("\t");
		sb.append(getFalseNegative());
		sb.append("\t");
		sb.append(getPositiveRecall());
		sb.append("\t");
		sb.append(getPositivePrecision());
		sb.append("\t");
		sb.append(getPositiveError());
		return sb.toString();
	}
}
