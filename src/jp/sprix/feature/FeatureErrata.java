package jp.sprix.feature;

import java.math.BigDecimal;
import java.util.ArrayList;

import jp.sprix.cv.CVErrataGeometry;

public class FeatureErrata {

	/**
	 * errata行から特徴量を取得する
	 * 
	 * @param errataLine
	 * @return
	 */
	public static double[] getFeature(String[] errataLine) {
		// 座標情報を取り出す
		ArrayList<double[]> coordinates = new ArrayList<double[]>();
		if (errataLine[3].equals("NaN") || errataLine[4].equals("NaN")
				|| errataLine[5].equals("NaN") || errataLine[6].equals("NaN")
				|| errataLine[7].equals("NaN") || errataLine[8].equals("NaN")
				|| errataLine[9].equals("NaN") || errataLine[10].equals("NaN")) {
			// NaNでも事例として登録する
			double[] coordinateNaN = { Double.NaN, Double.NaN };
			coordinates.add(coordinateNaN);
			coordinates.add(coordinateNaN);
			coordinates.add(coordinateNaN);
			coordinates.add(coordinateNaN);

		} else {
			double[] coordinate1 = { Double.parseDouble(errataLine[3]),
					Double.parseDouble(errataLine[4]) };
			double[] coordinate2 = { Double.parseDouble(errataLine[5]),
					Double.parseDouble(errataLine[6]) };
			double[] coordinate3 = { Double.parseDouble(errataLine[7]),
					Double.parseDouble(errataLine[8]) };
			double[] coordinate4 = { Double.parseDouble(errataLine[9]),
					Double.parseDouble(errataLine[10]) };
			coordinates.add(coordinate1);
			coordinates.add(coordinate2);
			coordinates.add(coordinate3);
			coordinates.add(coordinate4);
		}

		// 特徴量を抽出する
		double angle0 = CVErrataGeometry.getInteriorAngle(coordinates, 0);
		double angle1 = CVErrataGeometry.getInteriorAngle(coordinates, 1);
		double angle2 = CVErrataGeometry.getInteriorAngle(coordinates, 2);
		double angle3 = CVErrataGeometry.getInteriorAngle(coordinates, 3);
		double perimeter = CVErrataGeometry.getPerimeter(coordinates);
		double inner0 = CVErrataGeometry.getInner(coordinates, 0);
		double inner1 = CVErrataGeometry.getInner(coordinates, 1);
		double score = Double.parseDouble(errataLine[11]);
		double norm0 = CVErrataGeometry.getNormRatioSideBySide(coordinates, 0);
		double norm1 = CVErrataGeometry.getNormRatioSideBySide(coordinates, 1);
		double aspect = CVErrataGeometry.getAspectRatio(coordinates);
		double point0x = coordinates.get(0)[0];
		double point0y = coordinates.get(0)[1];
		double point1x = coordinates.get(1)[0];
		double point1y = coordinates.get(1)[1];
		double point2x = coordinates.get(2)[0];
		double point2y = coordinates.get(2)[1];
		double point3x = coordinates.get(3)[0];
		double point3y = coordinates.get(3)[1];

		double[] features = { angle0, angle1, angle2, angle3, perimeter, inner0, inner1, score,
				norm0, norm1, aspect, point0x, point0y , point1x, point1y, point2x, point2y, point3x, point3y};

		return features;
	}

	/**
	 * double値のデータをintのスケールに変換する
	 * 
	 * @param data
	 * @return
	 */
	public static int[] scaleInteger(double[] data) {
		int[] features_int = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			// NaNであれば0とする
			if (Double.isNaN(data[i])) {
				features_int[i] = 0;
				continue;
			}

			final BigDecimal data_bd = new BigDecimal(data[i]);
			final BigDecimal data_scale_confficient = new BigDecimal(1000);
			// 少数第3位まで残す
			BigDecimal data_cut = data_bd.setScale(3, BigDecimal.ROUND_HALF_UP);

			// 1000倍してスケールを整える
			final BigDecimal data_scale = data_cut.multiply(data_scale_confficient);
			int data_int = data_scale.intValue();
			features_int[i] = data_int;
		}
		return features_int;
	}
}
