package jp.sprix.randomforest;

import java.awt.Point;

import jp.sprix.component.ComponentPointIntData;
import jp.sprix.sample.PointSample;

/**
 * ランダムフォレストのサンプル事例クラス
 * 
 * 事例=Point
 * 
 * @author root
 * 
 */
public class RandomForestSampleCase {
	/* サンプル配列 */
	Point[] cases = null;

	/* 特徴量配列[添字][特徴量配列] */
	int[][] features = null;

	int[] positiveOrNegative = null;

	private int positiveCnt = 0;

	private int negativeCnt = 0;

	/**
	 * サンプル事例
	 * 
	 * @param cnt
	 * @param max
	 */
	public RandomForestSampleCase(int cnt, int max, ComponentPointIntData componentPointData) {
		cases = new Point[cnt];
		features = new int[cnt][];
		positiveOrNegative = new int[cnt];
		for (int i = 0; i < cnt; i++) {
			// 1つのpoint
			Point point = PointSample.getRandomPointSample(max);
			double distance1 = componentPointData.getDistanseFrom1(point);
			double distance2 = componentPointData.getDistanseFrom2(point);
			// 特徴量追加
			double distance1and2 = componentPointData.getDistanseFrom1and2(point);

			// 1つのpointに対する特徴量の配列({x, y, distanse1, distanse2})
			int[] feature = { point.x, point.y, Math.round(Math.round(distance1)),
					Math.round(Math.round(distance2)), Math.round(Math.round(distance1and2)) };

			// 点
			cases[i] = point;

			// 特徴量
			features[i] = feature;

			// 正負判定
			positiveOrNegative[i] = componentPointData.setComponentPointData(point);
			if (positiveOrNegative[i] > 0) {
				positiveCnt++;
			} else {
				negativeCnt++;
			}
		}
	}

	/**
	 * 指定された添字配列の事例がすべて正事例かどうか
	 * 
	 * @param subscripts
	 *            事例の添字配列
	 * @return すべて正事例 true、負事例を含むfalse
	 */
	public boolean isAllPositive(int[] subscripts) {
		for (int i = 0; i < subscripts.length; i++) {
			if (positiveOrNegative[subscripts[i]] < 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 添字配列が表す事例がpositiveかどうか？
	 * 
	 * @param subscripts
	 * @return
	 */
	public int positiveCnt(int[] subscripts) {
		int cnt = 0;
		for (int i = 0; i < subscripts.length; i++) {
			if (positiveOrNegative[subscripts[i]] > 0) {
				cnt++;
			}
		}
		return cnt;
	}

	/**
	 * 指定された添字配列の事例がすべて負事例かどうか
	 * 
	 * @param subscripts
	 *            　事例の添字配列
	 * @return すべて負事例 true、正事例を含むfalse
	 */
	public boolean isAllNegative(int[] subscripts) {
		for (int i = 0; i < subscripts.length; i++) {
			if (positiveOrNegative[subscripts[i]] > 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 添字配列が表す事例がpositiveかどうか？
	 * 
	 * @param subscripts
	 * @return
	 */
	public int negativeCnt(int[] subscripts) {
		int cnt = 0;
		for (int i = 0; i < subscripts.length; i++) {
			if (positiveOrNegative[subscripts[i]] < 0) {
				cnt++;
			}
		}
		return cnt;
	}

	/* getter and setter */

	public int getPositiveCnt() {
		return positiveCnt;
	}

	public void setPositiveCnt(int positiveCnt) {
		this.positiveCnt = positiveCnt;
	}

	public int getNegativeCnt() {
		return negativeCnt;
	}

	public void setNegativeCnt(int negativeCnt) {
		this.negativeCnt = negativeCnt;
	}

	public Point[] getCases() {
		return cases;
	}

	public void setCases(Point[] cases) {
		this.cases = cases;
	}

	public int[] getPositiveOrNegative() {
		return positiveOrNegative;
	}

	public void setPositiveOrNegative(int[] positiveOrNegative) {
		this.positiveOrNegative = positiveOrNegative;
	}

}
