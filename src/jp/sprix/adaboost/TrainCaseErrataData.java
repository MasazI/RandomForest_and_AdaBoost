package jp.sprix.adaboost;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import jp.sprix.component.ComponentData;
import jp.sprix.component.ComponentErrataData;
import jp.sprix.cv.CVPointGeometry;
import jp.sprix.io.Import;
import jp.sprix.learning.data.CaseData;
import jp.sprix.learning.data.CoordinateCaseData;

public class TrainCaseErrataData extends TrainCaseData {
	private String[][] erratas = null;

	public TrainCaseErrataData(ComponentData componentData) {
		int sampleNum = componentData.getSampleNumber();
		sampleNames = new String[sampleNum];
		caseDatas = new CaseData[sampleNum];
		twoclasses = new int[sampleNum];
		weightes = new double[sampleNum];
		erratas = new String[sampleNum][];

		// CaseDataとして、座標の配列を使用する
		Properties prop = Import.importProperty();
		double positiveWeight = Double.parseDouble(prop.getProperty("adaboost.positive.weight"));
		double negativeWeight = Double.parseDouble(prop.getProperty("adaboost.negative.weight"));

		// make caseDatas
		double normalizationValue = 0;

		HashMap<String[], String> samples = ((ComponentErrataData) componentData).getSamples();
		Set<String[]> lines = samples.keySet();

		int l = 0;
		for (String[] line : lines) {
			// errataデータ配列
			erratas[l] = line;

			// 事例名前配列
			sampleNames[l] = line[0];

			// 点を登録する
			Point2D.Double[] points = CVPointGeometry.getPointsFromErrata(line);
			CoordinateCaseData coordinateCaseData = new CoordinateCaseData();
			coordinateCaseData.setmSampleName(line[0]);
			for (int i = 0; i < points.length; i++) {
				coordinateCaseData.addCoordinate(points[i]);
			}
			coordinateCaseData.setScore(Integer.parseInt(line[line.length - 2]));
			caseDatas[l] = coordinateCaseData;
			String type = line[line.length - 1];
			if (type.equals("+")) {
				twoclasses[l] = 1;
				weightes[l] = (double) positiveWeight;
				normalizationValue += positiveWeight;
			} else {
				twoclasses[l] = -1;
				weightes[l] = (double) negativeWeight;
				normalizationValue += negativeWeight;
			}

			l++;
		}

		// 重みの正規化
		for (int i = 0; i < sampleNum; i++) {
			weightes[i] = weightes[i] / normalizationValue;
		}
	}

	public String[][] getErratas() {
		return erratas;
	}
}
