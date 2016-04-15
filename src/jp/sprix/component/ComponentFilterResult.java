package jp.sprix.component;

import jp.sprix.threshold.Threshold;

/**
 * 
 * 
 * @author root
 *
 */
public class ComponentFilterResult {
	private double FMeasure = 0;

	private double precision = 0;

	private double recall = 0;

	private Threshold threshold = null;

	public ComponentFilterResult(double f, Threshold t) {
		FMeasure = f;
		threshold = t;
	}

	// getter and setter

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	public double getFMeasure() {
		return FMeasure;
	}

	public void setFMeasure(double fMeasure) {
		FMeasure = fMeasure;
	}

	public Threshold getThreshold() {
		return threshold;
	}

	public void setThreshold(Threshold threshold) {
		this.threshold = threshold;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}
}
