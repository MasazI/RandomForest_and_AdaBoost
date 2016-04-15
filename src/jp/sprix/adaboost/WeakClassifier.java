package jp.sprix.adaboost;

import java.awt.geom.Point2D;

import jp.sprix.component.ComponentPointDoubleData;
import jp.sprix.learning.data.CoordinateCaseData;
import jp.sprix.threshold.Threshold;
import jp.sprix.threshold.PointValueThreshold;
import jp.sprix.threshold.CoordinatesThreshold;

/**
 * 弱識別器クラス
 * 
 * @author root
 * 
 */
public class WeakClassifier {
	Threshold threshold = null;

	public WeakClassifier(Threshold threshold) {
		this.threshold = threshold;
	}

	// getter and setter

	public Threshold getThreshold() {
		return threshold;
	}

	public void setThreshold(Threshold threshold) {
		this.threshold = threshold;
	}

	public boolean isFinish() {
		return threshold.isFinish();
	}

	public void resetFinish() {
		threshold.resetThreshold();
	}

	public int getPointTwoCassifyValue(Point2D.Double point, ComponentPointDoubleData componentPointData) {
		return ((PointValueThreshold) threshold).getTwoCassifyValue(point, componentPointData);
	}

	public int getCoordinateTwoCassifyValue(CoordinateCaseData data) {
		return ((CoordinatesThreshold) threshold).getTwoCassifyValue(data);
	}

	public void next() {
		threshold.next();
	}
}
