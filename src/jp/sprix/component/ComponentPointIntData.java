package jp.sprix.component;

import java.awt.Point;
import java.util.HashMap;
import java.util.Properties;

import jp.sprix.cv.CVPointGeometry;
import jp.sprix.io.Import;
import jp.sprix.randomforest.RandomForestSampleCase;

/**
 * int型の座標点用コンポーネント
 * 
 * @author root
 * 
 */
public class ComponentPointIntData extends ComponentData {
	// ランダムな座標点を取得する矩形のx、yのMax
	private int max = 0;

	// 正事例円の半径
	private int radius = 0;

	// 正事例円の中心点1
	private Point center1 = null;

	// 正事例円の中心点2
	private Point center2 = null;

	/**
	 * すべての要素を格納するMap
	 */
	private HashMap<Point, String> samples = null;

	public ComponentPointIntData(String type) {
		// 要素数
		int allcnt = 0;
		// typeに合わせた初期化
		Properties prop = Import.importProperty();
		if (type.equals("adaboost")) {
			allcnt = Integer.parseInt(prop.getProperty("adaboost.sample.allcnt"));
			max = Integer.parseInt(prop.getProperty("adaboost.sample.max"));
			radius = Integer.parseInt(prop.getProperty("adaboost.sample.radius"));
			center1 = new Point();
			center1.setLocation(Integer.parseInt(prop.getProperty("adaboost.sample.circle1x")),
					Integer.parseInt(prop.getProperty("adaboost.sample.circle1y")));
			center2 = new Point();
			center2.setLocation(Integer.parseInt(prop.getProperty("adaboost.sample.circle2x")),
					Integer.parseInt(prop.getProperty("adaboost.sample.circle2y")));
			super.setSampleNumber(Integer.parseInt(prop.getProperty("adaboost.sample.allcnt")));
		} else if (type.equals("randomforest")) {
			allcnt = Integer.parseInt(prop.getProperty("randomforest.sample.allcnt"));
			max = Integer.parseInt(prop.getProperty("randomforest.sample.max"));
			radius = Integer.parseInt(prop.getProperty("randomforest.sample.radius"));
			center1 = new Point();
			center1.setLocation(Integer.parseInt(prop.getProperty("randomforest.sample.circle1x")),
					Integer.parseInt(prop.getProperty("randomforest.sample.circle1y")));
			center2 = new Point();
			center2.setLocation(Integer.parseInt(prop.getProperty("randomforest.sample.circle2x")),
					Integer.parseInt(prop.getProperty("randomforest.sample.circle2y")));
			super.setSampleNumber(Integer.parseInt(prop.getProperty("randomforest.sample.allcnt")));
		}

		// 要素数に合わせた初期設定を行う
		samples = new HashMap<Point, String>(allcnt * 4 / 3);
	}

	/**
	 * コンポーネントにポイントデータを設定する
	 * 
	 * @param sample
	 * @return
	 */
	public int setComponentPointData(Point sample) {
		if (isPositive(sample)) {
			setSamplePositiveInc();
			samples.put(sample, "+");
			return 1;
		} else {
			setSampleNegativeInc();
			samples.put(sample, "-");
			return -1;
		}
	}

	/**
	 * ポイントが本コンポーネントの正事例に入るかどうかを見る
	 * 
	 * @param sample
	 * @return
	 */
	public boolean isPositive(Point sample) {
		boolean inCircle1 = false;
		boolean inCircle2 = false;
		if (CVPointGeometry.getDistanceInt(center1, sample) <= radius) {
			inCircle1 = true;
		}
		if (CVPointGeometry.getDistanceInt(center2, sample) <= radius) {
			inCircle2 = true;
		}

		if ((inCircle1 && !inCircle2) || (!inCircle1 && inCircle2)) {
			return true;
		} else {
			return false;
		}
	}

	public double getDistanseFrom1(Point point) {
		return CVPointGeometry.getDistanceInt(center1, point);
	}

	public double getDistanseFrom2(Point point) {
		return CVPointGeometry.getDistanceInt(center2, point);
	}

	public double getDistanseFrom1and2(Point point) {
		return CVPointGeometry.getDistanceInt(center1, point)
				+ CVPointGeometry.getDistanceInt(center2, point);
	}

	public HashMap<Point, String> getSamples() {
		return samples;
	}

	public void setSamples(HashMap<Point, String> samples) {
		this.samples = samples;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public Point getCenter1() {
		return center1;
	}

	public void setCenter1(Point center1) {
		this.center1 = center1;
	}

	public Point getCenter2() {
		return center2;
	}

	public void setCenter2(Point center2) {
		this.center2 = center2;
	}

	/**
	 * sampleケースから構築する
	 * 
	 * @param sampleCase
	 */
	public void rebuild(RandomForestSampleCase sampleCase) {
		samples = new HashMap<Point, String>(1000 * 4 / 3);
		Point[] points = sampleCase.getCases();
		int[] results = sampleCase.getPositiveOrNegative();

		for (int i = 0; i < points.length; i++) {
			if (results[i] > 0) {
				samples.put(points[i], "+");
			} else {
				samples.put(points[i], "-");
			}
		}
	}
}
