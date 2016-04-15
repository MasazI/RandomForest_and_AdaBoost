package jp.sprix.adaboost;

import jp.sprix.learning.data.CaseData;

/**
 * 教師付き事例
 * 
 * @author root
 * 
 */
public class TrainCaseData {
	// 事例の名前配列
	String[] sampleNames = null;

	// 事例の配列
	CaseData[] caseDatas = null;

	// 2値クラスの配列(事例の添字と対応)
	int[] twoclasses = null;

	// 重み配列(事例の添字と対応)
	double[] weightes = null;

	public String[] getSampleNames() {
		return sampleNames;
	}

	public void setSampleNames(String[] sampleNames) {
		this.sampleNames = sampleNames;
	}

	public CaseData[] getCaseDatas() {
		return caseDatas;
	}

	public int[] getTwoclasses() {
		return twoclasses;
	}

	public double[] getWeightes() {
		return weightes;
	}
}
