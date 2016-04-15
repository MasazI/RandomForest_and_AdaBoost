package jp.sprix.adaboost;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Set;

import jp.sprix.component.ComponentData;
import jp.sprix.component.ComponentErrataData;
import jp.sprix.cv.CVPointGeometry;
import jp.sprix.learning.data.CaseData;
import jp.sprix.learning.data.CoordinateCaseData;

public class QueryCaseErrataData extends TrainCaseData {
	private String[][] erratas = null;

	public QueryCaseErrataData(ComponentData componentData) {
		int sampleNum = componentData.getSampleNumber();
		sampleNames = new String[sampleNum];
		caseDatas = new CaseData[sampleNum];
		twoclasses = new int[sampleNum];
		erratas = new String[sampleNum][];

		HashMap<String[], String> samples = ((ComponentErrataData) componentData).getSamples();
		Set<String[]> lines = samples.keySet();

		int l = 0;
		for (String[] line : lines) {
			// sample名
			sampleNames[l] = line[0];
			erratas[l] = line;
			// 点を登録する
			Point2D.Double[] points = CVPointGeometry.getPointsFromErrata(line);
			CoordinateCaseData coordinateCaseData = new CoordinateCaseData();
			for (int i = 0; i < points.length; i++) {
				coordinateCaseData.addCoordinate(points[i]);
			}
			coordinateCaseData.setScore(Integer.parseInt(line[line.length - 2]));
			caseDatas[l] = coordinateCaseData;
			String type = line[line.length - 1];
			if (type.equals("+")) {
				twoclasses[l] = 1;
			} else {
				twoclasses[l] = -1;
			}
			l++;
		}
	}

	public String[][] getErratas() {
		return erratas;
	}
}
