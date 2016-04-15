package jp.sprix.component;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Properties;

import jp.sprix.cv.CVPointGeometry;
import jp.sprix.io.Import;
import jp.sprix.randomforest.RandomForestSampleCase;

/**
 * double型の座標点用コンポーネント
 * 
 * @author root
 * 
 */
public class ComponentPointDoubleData extends ComponentData {
	// ランダムな座標点を取得する矩形のx、yのMax
	private int max = 0;

	// 正事例円の半径
	private int radius = 0;

	// 正事例円の中心点1
	private Point2D.Double center1 = null;

	// 正事例円の中心点2
	private Point2D.Double center2 = null;

	/**
	 * すべての要素を格納するMap
	 */
	private HashMap<Point2D.Double, String> samples = null;

	public ComponentPointDoubleData(String type) {
		Properties prop = Import.importProperty();
		if (type.equals("adaboost")) {
			max = Integer.parseInt(prop.getProperty("adaboost.sample.max"));
			radius = Integer.parseInt(prop.getProperty("adaboost.sample.radius"));
			center1 = new Point2D.Double();
			center1.setLocation(Double.parseDouble(prop.getProperty("adaboost.sample.circle1x")),
					Double.parseDouble(prop.getProperty("adaboost.sample.circle1y")));
			center2 = new Point2D.Double();
			center2.setLocation(Double.parseDouble(prop.getProperty("adaboost.sample.circle2x")),
					Double.parseDouble(prop.getProperty("adaboost.sample.circle2y")));
			super.setSampleNumber(Integer.parseInt(prop.getProperty("adaboost.sample.allcnt")));
		} else if (type.equals("randomforest")) {
			max = Integer.parseInt(prop.getProperty("randomforest.sample.max"));
			radius = Integer.parseInt(prop.getProperty("randomforest.sample.radius"));
			center1 = new Point2D.Double();
			center1.setLocation(
					Double.parseDouble(prop.getProperty("randomforest.sample.circle1x")),
					Double.parseDouble(prop.getProperty("randomforest.sample.circle1y")));
			center2 = new Point2D.Double();
			center2.setLocation(
					Double.parseDouble(prop.getProperty("randomforest.sample.circle2x")),
					Double.parseDouble(prop.getProperty("randomforest.sample.circle2y")));
			super.setSampleNumber(Integer.parseInt(prop.getProperty("randomforest.sample.allcnt")));
		}

		// 要素数に合わせた初期設定を行う
		samples = new HashMap<Point2D.Double, String>(max * 4 / 3);
	}

	/**
	 * コンポーネントにポイントデータを設定する
	 * 
	 * @param sample
	 * @return
	 */
	public int setComponentPointData(Point2D.Double sample) {
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
	public boolean isPositive(Point2D.Double sample) {
		boolean inCircle1 = false;
		boolean inCircle2 = false;
		if (CVPointGeometry.getDistance(center1, sample) <= radius) {
			inCircle1 = true;
		}
		if (CVPointGeometry.getDistance(center2, sample) <= radius) {
			inCircle2 = true;
		}

		if ((inCircle1 && !inCircle2) || (!inCircle1 && inCircle2)) {
			return true;
		} else {
			return false;
		}
	}

	public double getDistanseFrom1(Point2D.Double point) {
		return CVPointGeometry.getDistance(center1, point);
	}

	public double getDistanseFrom2(Point2D.Double point) {
		return CVPointGeometry.getDistance(center2, point);
	}

	public HashMap<Point2D.Double, String> getSamples() {
		return samples;
	}

	public void setSamples(HashMap<Point2D.Double, String> samples) {
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

	public Point2D.Double getCenter1() {
		return center1;
	}

	public void setCenter1(Point2D.Double center1) {
		this.center1 = center1;
	}

	public Point2D.Double getCenter2() {
		return center2;
	}

	public void setCenter2(Point2D.Double center2) {
		this.center2 = center2;
	}



}
