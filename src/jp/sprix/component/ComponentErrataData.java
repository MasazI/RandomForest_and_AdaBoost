package jp.sprix.component;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 各フィルターの入出力データ
 * 
 * @author root
 * 
 */
public class ComponentErrataData extends ComponentData {
	/**
	 * すべての要素を格納するMap
	 */
	private HashMap<String[], String> samples = new HashMap<String[], String>();

	/**
	 * コンストラクタ
	 * 
	 * errataのリストデータから、コンポーネントの母集団を初期化する
	 * 
	 * @param ArrayList
	 *            <String> errataのリストデータ
	 */
	public ComponentErrataData(ArrayList<String[]> errataLines) {
		setSampleNumber(errataLines.size());

		for (String[] lineSplitTab : errataLines) {
			String sampleName = lineSplitTab[0];
			String type = lineSplitTab[lineSplitTab.length - 1];
			if (type.equals("+")) {
				setSamplePositiveInc();
			} else if (type.equals("-")) {
				setSampleNegativeInc();
			}
			samples.put(lineSplitTab, type);
		}
	}

	public HashMap<String[], String> getSamples() {
		return samples;
	}

	public void setSamples(HashMap<String[], String> samples) {
		this.samples = samples;
	}

}
