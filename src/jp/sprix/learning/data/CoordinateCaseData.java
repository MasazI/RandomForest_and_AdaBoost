package jp.sprix.learning.data;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * 座標データの事例用クラス
 * 
 * 追加した分の座標を一つの事例とする
 * 
 * @author root
 * 
 */
public class CoordinateCaseData extends CaseData {
	// 事例名
	String mSampleName = null;

	// 座標データの入れ物
	ArrayList<Point2D.Double> pointList = null;

	// Score情報
	public int score = 0;

	/**
	 * 指定した個数の座標を1セットの事例とする
	 */
	public CoordinateCaseData() {
		pointList = new ArrayList<Point2D.Double>();
	}

	/**
	 * 座標の追加
	 * 
	 * @param Point2D
	 *            .Double point
	 */
	public void addCoordinate(Point2D.Double point) {
		pointList.add(point);
	}

	/**
	 * 座標のリストを取得
	 * 
	 * @return
	 */
	public ArrayList<Point2D.Double> getPointList() {
		return pointList;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getmSampleName() {
		return mSampleName;
	}

	public void setmSampleName(String mSampleName) {
		this.mSampleName = mSampleName;
	}
}
