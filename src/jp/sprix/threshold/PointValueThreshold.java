package jp.sprix.threshold;

import java.awt.geom.Point2D;
import java.util.Properties;

import jp.sprix.component.ComponentPointDoubleData;
import jp.sprix.io.Import;

public class PointValueThreshold implements Threshold {
	// 閾値初期値
	private double thresholdValue = 0;

	// 閾値の最大値初期値
	private double thresholdMaxValue;

	// 閾値のタイプ
	private String type = null;

	// この閾値をもつ弱識別器が完了したかどうか
	private boolean isFinish = false;

	/**
	 * コンストラクタ type指定なし
	 */
	public PointValueThreshold() {
		Properties prop = Import.importProperty();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param type
	 *            閾値のタイプを指定する(xMax, xMin, yMax, yMin, radius)
	 */
	public PointValueThreshold(String type) {
		this.type = type;
		setValue();
	}

	public void next() {
		thresholdValue++;
		if (thresholdValue > thresholdMaxValue) {
			isFinish = true;
		}
	}

	public boolean isFinish() {
		return isFinish;
	}

	public void resetThreshold() {
		setValue();
		isFinish = false;
	}

	private void setValue() {
		Properties prop = Import.importProperty();
		if (type.endsWith("xMax") || type.endsWith("yMax")) {
			thresholdValue = Double
					.parseDouble(prop.getProperty("adaboost.pointvalue.formaxstart"));
			thresholdMaxValue = Double.parseDouble(prop
					.getProperty("adaboost.pointvalue.formaxend"));
		} else if (type.endsWith("xMin") || type.endsWith("yMin")) {
			thresholdValue = Double
					.parseDouble(prop.getProperty("adaboost.pointvalue.forminstart"));
			thresholdMaxValue = Double.parseDouble(prop
					.getProperty("adaboost.pointvalue.forminend"));
		} else if (type.endsWith("distanseMax1") || type.endsWith("distanseMax2")
				|| type.endsWith("distanseMin1") || type.endsWith("distanseMin2")) {
			thresholdValue = Double.parseDouble(prop
					.getProperty("adaboost.pointvalue.distansestart"));
			thresholdMaxValue = Double.parseDouble(prop
					.getProperty("adaboost.pointvalue.distanseend"));
		}
	}

	/**
	 * 2クラスに分類した数値結果を返す
	 * 
	 * @param point
	 * @param componentPointData
	 * @return
	 */
	public int getTwoCassifyValue(Point2D.Double point, ComponentPointDoubleData componentPointData) {
		if (type.equals("xMax")) {
			if (point.getX() <= thresholdValue) {
				return 1;
			}
		} else if (type.equals("xMin")) {
			if (point.getX() >= thresholdValue) {
				return 1;
			}
		} else if (type.equals("yMax")) {
			if (point.getY() <= thresholdValue) {
				return 1;
			}
		} else if (type.equals("yMin")) {
			if (point.getY() >= thresholdValue) {
				return 1;
			}
		} else if (type.endsWith("distanseMax1")) {
			if (componentPointData.getDistanseFrom1(point) <= thresholdValue) {
				return 1;
			}
		} else if (type.endsWith("distanseMax2")) {
			if (componentPointData.getDistanseFrom2(point) <= thresholdValue) {
				return 1;
			}
		} else if (type.endsWith("distanseMin1")) {
			if (componentPointData.getDistanseFrom1(point) >= thresholdValue) {
				return 1;
			}
		} else if (type.endsWith("distanseMin2")) {
			if (componentPointData.getDistanseFrom2(point) >= thresholdValue) {
				return 1;
			}
		}
		return -1;

	}

	@Override
	public String output() {
		return type + "\t" + thresholdValue;
	}

	@Override
	public void importThreshold(String thresholdStr) {
		String[] thresholdArray = thresholdStr.split("\t");
		type = thresholdArray[0];
		thresholdValue = Double.parseDouble(thresholdArray[1]);
	}

	// getter and setter

	public double getThresholdValue() {
		return thresholdValue;
	}

	public void setThresholdValue(double thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
