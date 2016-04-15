package jp.sprix.learning.filter;

import jp.sprix.threshold.CoordinatesThreshold;

/**
 * フィルターの実行クラス
 * 
 * @author root
 * 
 */
public class CoordinateFilterExecuter {
	CoordinatesThreshold threshold = null;

	/**
	 * コンストラクタ
	 */
	public CoordinateFilterExecuter(CoordinatesThreshold threshold) {
		this.threshold = threshold;
	}

	/**
	 * 内角の閾値
	 * 
	 * @param angle
	 *            角度
	 * @return 閾値に含まれればtrue、含まれなければfalse
	 */
/*	public boolean executeInteriorAngle(double angle) {
		double min = threshold.getAngleMinThreshold(true);
		double max = threshold.getAngleMaxThreshold(true);

		if (min <= angle && angle <= max) {
			return true;
		} else {
			return false;
		}
	}*/

	/**
	 * 周囲の閾値
	 * 
	 * @param perimeter
	 *            周囲
	 * @return 閾値に含まれればtrue、含まれなければfalse
	 */
/*	public boolean executePerimeter(double perimeter) {
		double min = threshold.getDefaultPerimeterMinThreshold(true);
		double max = threshold.getDefaultPerimeterMaxThreshold(true);

		if (min <= perimeter && perimeter <= max) {
			return true;
		} else {
			return false;
		}
	}*/

	/**
	 * 内積の閾値
	 * 
	 * @param inner
	 *            内積
	 * @return 境界内ならtrue、以外ならfalse
	 */
	public boolean executeInner(double inner) {
		double boundary = threshold.getInnerthreshold();
		if (inner > boundary) {
			return true;
		} else {
			return false;
		}
	}
}
