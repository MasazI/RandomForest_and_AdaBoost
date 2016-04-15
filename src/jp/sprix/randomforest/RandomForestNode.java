package jp.sprix.randomforest;

import java.util.ArrayList;
import java.util.Properties;

import jp.sprix.io.Import;

/**
 * ランダムフォレストの分離関数
 * 
 * @author root
 * 
 */
public class RandomForestNode {
	// プロパティ
	Properties prop = null;

	/* 分離関数の実行に必要なパラメータ */
	// 分離関数のタイプ
	private String type = null;

	// 分離関数の閾値
	private int threshold = 0;

	// 分離関数の閾値エンド
	private int thresholdEnd = 0;

	// 分離関数の閾値ステップ
	private int thresholdStep = 0;

	// 大なり（以上）かどうか
	private int isMore = 1;

	/* 分離関数を選択中に変化する */
	// 閾値反転フラグ
	private boolean isTurnOver = false;

	// 閾値終了フラグ
	private boolean isFinish = false;

	/* 子ノードの情報 */
	// 子ノード(Left)
	private RandomForestNode childLeftNode = null;

	// 子ノード(Right)
	private RandomForestNode childRightNode = null;

	// 先端ノード
	private boolean isLeaf = false;

	// 分類カテゴリー
	private int category = 0;

	/**
	 * 分離関数コンストラクタ
	 * 
	 * @param prop
	 * @param type
	 * @param isTopNode
	 * @param treeNum
	 * @param nodeNum
	 */
	public RandomForestNode(Properties prop, String type, boolean isTopNode) {
		// node番号の設定
		this.prop = prop;
		// 分離関数タイプを設定
		this.type = type;
		// 分離関数の閾値を設定
		setThresholdValue();
	}

	/**
	 * インポート用コンストラクタ
	 */
	public RandomForestNode() {

	}

	/**
	 * 特徴量配列が閾値に適合するかどうか
	 * 
	 * @param features
	 *            特徴量の配列({x, y, distanse1, distanse2})
	 * @return 満たす1、満たさない-1
	 */
	public int isAdaptiveFeature(int[] features) {
		int num = 0;
		if (type.equals("x")) {
			num = 0;
		} else if (type.equals("y")) {
			num = 1;
		} else if (type.equals("distanse1")) {
			num = 2;
		} else if (type.equals("distanse2")) {
			num = 3;
		} else if (type.equals("distanse12")) {
			// 追加特徴量
			num = 4;
		}
		return isAdaptive(features[num]);
	}

	/**
	 * 指定した値が閾値に適合するかどうか
	 * 
	 * @param value
	 * @return 満たす1、満たさない-1
	 */
	private int isAdaptive(int value) {
		if (isMore > 0) {
			if (value > threshold) {
				return 1;
			}
		} else {
			if (value < threshold) {
				return 1;
			}
		}
		return -1;
	}

	/**
	 * 閾値パラメータを次へ進める
	 */
	public void nextThreshold() {
		threshold += thresholdStep;
		setFinish();
	}

	/**
	 * 閾値のフラグを設定する
	 * 
	 */
	public void setFinish() {
		if (threshold >= thresholdEnd) {
			if (isTurnOver) {
				// 大なり小なりが反転済みなら終了
				isFinish = true;
			} else {
				// 大なり小なりを反転
				isTurnOver = true;
				isMore = -1;
				// 閾値をリセット
				setThresholdValue();
			}
		}
	}

	/**
	 * 閾値パラメータの終了
	 */
	public boolean isFinish() {
		return isFinish;
	}

	/**
	 * 分離関数の閾値を設定する
	 * 
	 * @param prop
	 * @param type
	 */
	public void setThresholdValue() {
		// 分離関数タイプによって閾値の初期値を設定する
		if (type.equals("x") || type.equals("y")) {
			// x軸またはy軸
			threshold = Integer.parseInt(prop.getProperty("randomforest.pointvalue.linestart"));
			thresholdEnd = Integer.parseInt(prop.getProperty("randomforest.pointvalue.lineend"));
			thresholdStep = Integer.parseInt(prop.getProperty("randomforest.pointvalue.linestep"));
		} else if (type.equals("distanse1") || type.equals("distanse2")) {
			// 点1または点2からの距離
			threshold = Integer.parseInt(prop.getProperty("randomforest.pointvalue.distansestart"));
			thresholdEnd = Integer
					.parseInt(prop.getProperty("randomforest.pointvalue.distanseend"));
			thresholdStep = Integer.parseInt(prop
					.getProperty("randomforest.pointvalue.distansestep"));
		} else if (type.equals("distanse12")) {
			// 閾値追加（点1と点2からの距離の和）
			threshold = Integer.parseInt(prop.getProperty("randomforest.pointvalue.distansestart"));
			thresholdEnd = Integer.parseInt(prop
					.getProperty("randomforest.pointvalue.distanseendplus"));
			thresholdStep = Integer.parseInt(prop
					.getProperty("randomforest.pointvalue.distansestep"));
		}
	}

	/**
	 * 閾値文字列を返す
	 * 
	 * @return
	 */
	public String output() {
		StringBuilder sb = new StringBuilder();
		sb.append(type); // 0
		sb.append("\t");
		sb.append(threshold); // 1
		sb.append("\t");
		sb.append(isMore); // 2
		sb.append("\t");
		sb.append(isLeaf); // 3
		sb.append("\t");
		sb.append(category); // 4

		return sb.toString();
	}

	/**
	 * 閾値文字列を適用する
	 */
	public void importThreshold(String thresholdStr) {
		String[] thresholdeStrArray = thresholdStr.split("\t");
		this.type = thresholdeStrArray[0];
		this.threshold = Integer.parseInt(thresholdeStrArray[1]);
		this.isMore = Integer.parseInt(thresholdeStrArray[2]);
		this.isLeaf = Boolean.getBoolean(thresholdeStrArray[3]);
		this.category = Integer.parseInt(thresholdeStrArray[4]);
	}

	/**
	 * 現在の閾値で、L(正事例と判断できる添字配列を返す)
	 * 
	 * @param samplingNum
	 *            分離する添字配列
	 * @param allSampleCase
	 *            添字に対応した特徴量を取得するための事例オブジェクト
	 * @return 分離によって得られる正事例添字配列
	 */
	public int[] getSamplingL(int[] samplingNum, RandomForestSampleCase allSampleCase) {
		ArrayList<Integer> tmpList = new ArrayList<Integer>();
		for (int i = 0; i < samplingNum.length; i++) {
			// 正と判断
			if (isAdaptiveFeature(allSampleCase.features[samplingNum[i]]) > 0) {
				tmpList.add(samplingNum[i]);
			}
		}
		int[] samplingL = new int[tmpList.size()];
		for (int i = 0; i < tmpList.size(); i++) {
			samplingL[i] = tmpList.get(i);
		}
		return samplingL;
	}

	/**
	 * 現在の閾値で、L(負事例と判断できる添字配列を返す)
	 * 
	 * @param samplingNum
	 *            分離する添字配列
	 * @param allSampleCase
	 *            添字に対応した特徴量を取得するための事例オブジェクト
	 * @return 分離によって得られる負事例添字配列
	 */
	public int[] getSamplingR(int[] samplingNum, RandomForestSampleCase allSampleCase) {
		ArrayList<Integer> tmpList = new ArrayList<Integer>();
		for (int i = 0; i < samplingNum.length; i++) {
			// 負と判断
			if (isAdaptiveFeature(allSampleCase.features[samplingNum[i]]) < 0) {
				tmpList.add(samplingNum[i]);
			}
		}
		int[] samplingR = new int[tmpList.size()];
		for (int i = 0; i < tmpList.size(); i++) {
			samplingR[i] = tmpList.get(i);
		}
		return samplingR;
	}

	/* setter and getter */

	public RandomForestNode getChildLeftNode() {
		return childLeftNode;
	}

	public void setChildLeftNode(RandomForestNode childLeftNode) {
		this.childLeftNode = childLeftNode;
	}

	public RandomForestNode getChildRightNode() {
		return childRightNode;
	}

	public void setChildRightNode(RandomForestNode childRightNode) {
		this.childRightNode = childRightNode;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

}
