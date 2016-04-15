package jp.sprix.adaboost;

import java.awt.geom.Point2D;
import java.util.Properties;

import jp.sprix.component.ComponentData;
import jp.sprix.component.ComponentPointDoubleData;
import jp.sprix.io.Import;
import jp.sprix.learning.data.CaseData;
import jp.sprix.learning.data.CoordinateCaseData;
import jp.sprix.sample.PointSample;

public class TrainCasePointData extends TrainCaseData {
	public TrainCasePointData(ComponentData componentData) {
		int sampleNum = componentData.getSampleNumber();
		caseDatas = new CaseData[sampleNum];
		twoclasses = new int[sampleNum];
		weightes = new double[sampleNum];

		Properties prop = Import.importProperty();
		double positiveWeight = Double.parseDouble(prop.getProperty("adaboost.positive.weight"));
		double negativeWeight = Double.parseDouble(prop.getProperty("adaboost.negative.weight"));

		// make caseDatas
		double normalizationValue = 0;
		for (int i = 0; i < sampleNum; i++) {
			// make sample
			Point2D.Double sample = PointSample
					.getRandomPointDoubleSample(((ComponentPointDoubleData) componentData).getMax());

			// make case
			CoordinateCaseData coordinateCaseData = new CoordinateCaseData();
			coordinateCaseData.addCoordinate(sample);
			caseDatas[i] = coordinateCaseData;

			// set Component
			int category = ((ComponentPointDoubleData) componentData).setComponentPointData(sample);
			twoclasses[i] = category;

			if (category == 1) {
				weightes[i] = (double) positiveWeight;
				normalizationValue += positiveWeight;
			} else {
				weightes[i] = (double) negativeWeight;
				normalizationValue += negativeWeight;
			}
		}

		// 重みの正規化
		for (int i = 0; i < sampleNum; i++) {
			weightes[i] = weightes[i] / normalizationValue;
		}
	}
}
