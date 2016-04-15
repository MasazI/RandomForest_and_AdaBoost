package jp.sprix.cv;

import java.util.ArrayList;

public class CVErrataGeometry {
	/**
	 * 内角を取得する
	 * 
	 * @param ArrayList
	 *            <String[]> 頂点のリスト。要素はx, yの配列
	 * @return 座標点index（リストの格納順）
	 */
	public static double getInteriorAngle(ArrayList<double[]> coordinates, int index) {
		if (coordinates == null) {
			return 0;
		}
		if (coordinates.size() < 4) {
			return 0;
		}

		// 指定点
		int count = coordinates.size();
		// 点の最大値
		int maxNum = count - 1;
		if (index < 0 || maxNum < index) {
			// 指定された頂点の番号が不正
			return 0;
		}

		double[] angleCoordinate = coordinates.get(index);
		double centerX = angleCoordinate[0];
		double centerY = angleCoordinate[1];

		// 前、後の点
		int buff = index - 1;
		if (buff < 0) {
			while (buff < 0) {
				// 負だったら正になるまで点の数を足す
				buff += count;
			}
		}
		int previousIndex = buff % count;
		int nextIndex = (index + 1) % count;

		// 座標
		double[] previousCoordinate = coordinates.get(previousIndex);
		double previousX = previousCoordinate[0];
		double previousY = previousCoordinate[1];
		double[] nextCoordinate = coordinates.get(nextIndex);
		double nextX = nextCoordinate[0];
		double nextY = nextCoordinate[1];

		// ベクトル
		double vPreCenX = previousX - centerX;
		double vPreCenY = previousY - centerY;
		double vNextCenX = nextX - centerX;
		double vNextCenY = nextY - centerY;
		// double vNextScale = Math.sqrt(Math.pow(vNextCenX, 2) +
		// Math.pow(vNextCenY, 2));

		// 内積
		double vInner = vPreCenX * vNextCenX + vPreCenY * vNextCenY;
		// 外積
		double vOuter = vPreCenX * vNextCenY - vPreCenY * vNextCenX;
		// 逆tan
		double arcTan = Math.atan2(vOuter, vInner);
		// 度に変換
		double deg = Math.abs(Math.toDegrees(arcTan));
		if (Math.toDegrees(arcTan) > 0) {
			deg += 180;
		}

		return deg;
	}

	/**
	 * 指定した座標点を結んだ図形の周囲長を求める
	 * 
	 * @param coordinates
	 *            座標点
	 * @return double 周囲長
	 */
	public static double getPerimeter(ArrayList<double[]> coordinates) {
		if (coordinates == null) {
			return 0;
		}
		if (coordinates.size() < 4) {
			return 0;
		}

		// 頂点数
		int count = coordinates.size();

		// 周囲長
		double length = 0;
		for (int i = 0; i < count; i++) {
			length += getDistanceToNext(i, coordinates);
		}
		return length;
	}

	/**
	 * 指定した頂点の次の点までとの距離を求める
	 * 
	 * @param index
	 *            頂点
	 * @param 座標点の配列
	 * 
	 * @return double 次の頂点までとの距離
	 */
	public static double getDistanceToNext(int index, ArrayList<double[]> coordinates) {
		if (coordinates.size() < 0) {
			return 0;
		}

		// 指定した番号
		int count = coordinates.size();
		int maxCoordinateNum = count - 1;
		if (index < 0 || maxCoordinateNum < index) {
			// 指定された頂点の番号が不正
			return 0;
		}
		int nextIndex = (index + 1) % count;

		double iniPointX = coordinates.get(index)[0];
		double iniPointY = coordinates.get(index)[1];

		double termPointX = coordinates.get(nextIndex)[0];
		double termPointY = coordinates.get(nextIndex)[1];

		return Math.sqrt(Math.pow(termPointX - iniPointX, 2) + Math.pow(termPointY - iniPointY, 2));
	}

	/**
	 * 指定した座標点と時計周りに進んだ次の点を結んだ辺と隣合わない辺の内積計算
	 * 
	 * @param index
	 *            指定した座標
	 * @param coordinates
	 *            座評点
	 * @return　内積
	 */
	public static double getInner(ArrayList<double[]> coordinates, int index) {
		if (coordinates.size() < 0) {
			return 0;
		}

		int count = coordinates.size();
		int max_index = count - 1;
		if (index < 0 || max_index < index) {
			// 指定された頂点の番号が不正
			return 0;
		}
		int nextIndex = (index + 1) % count;

		double iniPointX = coordinates.get(index)[0];
		double iniPointY = coordinates.get(index)[1];

		double termPointX = coordinates.get(nextIndex)[0];
		double termPointY = coordinates.get(nextIndex)[1];

		double vectorX = termPointX - iniPointX;
		double vectorY = termPointY - iniPointY;

		// 隣合わない辺
		int oppIndex = (index + 2) % count;
		int termOppIndex = (oppIndex + 1) % count;

		double iniOppPointX = coordinates.get(oppIndex)[0];
		double iniOppPointY = coordinates.get(oppIndex)[1];

		double iniTermOppPointX = coordinates.get(termOppIndex)[0];
		double iniTremOppPointY = coordinates.get(termOppIndex)[1];

		double oppVectorX = iniOppPointX - iniTermOppPointX;
		double oppVectorY = iniOppPointY - iniTremOppPointY;

		// 正規化＆内積
		double scale = Math.sqrt(Math.pow(vectorX, 2) + Math.pow(vectorY, 2))
				* Math.sqrt(Math.pow(oppVectorX, 2) + Math.pow(oppVectorY, 2));
		if (scale == 0) {
			scale = 1;
		}
		double innderV = (vectorX * oppVectorX + vectorY * oppVectorY) / scale;
		return innderV;
	}

	/**
	 * 隣り合わない辺との比を取得する
	 * 
	 * @param coordinates
	 * @param index
	 * @return
	 */
	public static double getNormRatioSideBySide(ArrayList<double[]> coordinates, int index) {
		if (coordinates.size() < 0) {
			return 0;
		}
		int count = coordinates.size();
		int max_index = count - 1;
		if (index < 0 || max_index < index) {
			// 指定された頂点の番号が不正
			return 0;
		}
		double distance_ini = getDistanceToNext(index, coordinates);

		// 隣り合わない辺の始点
		int oppIndex = (index + 2) % count;
		double distance_opp = getDistanceToNext(oppIndex, coordinates);

		if (distance_opp == 0) {
			return 0;
		}

		double ratio = distance_ini / distance_opp;

		return ratio;
	}

	/**
	 * 縦と横のアスペクト比を取得する(4角形限定)
	 * 
	 * @param coordinates
	 * @param index
	 * @return
	 */
	public static double getAspectRatio(ArrayList<double[]> coordinates) {
		if (coordinates == null) {
			return 0;
		}
		if (coordinates.size() < 4) {
			return 0;
		}

		double width = getDistanceToNext(0, coordinates) + getDistanceToNext(2, coordinates);
		double height = getDistanceToNext(1, coordinates) + getDistanceToNext(3, coordinates);

		if (height == 0) {
			return 0;
		}

		return width / height;
	}

	/**
	 * errata行から座標情報などを取り出し、文字列で返す
	 * 
	 * @param errataLine
	 * @param scale
	 * @return
	 */
	public static String getOutputString(String[] errataLine, double scale) {
		double angle0 = 0;
		double angle1 = 0;
		double angle2 = 0;
		double angle3 = 0;
		double perimeter = 0;
		double inner0 = 0;
		double inner1 = 0;
		double norm0 = 0;
		double norm1 = 0;
		double aspect = 0;

		// 座標情報を取り出す
		if (errataLine[3].equals("NaN") || errataLine[4].equals("NaN")
				|| errataLine[5].equals("NaN") || errataLine[6].equals("NaN")
				|| errataLine[7].equals("NaN") || errataLine[8].equals("NaN")
				|| errataLine[9].equals("NaN") || errataLine[10].equals("NaN")) {
		} else {
			double[] coordinate1 = { Double.parseDouble(errataLine[3]) / scale,
					Double.parseDouble(errataLine[4]) / scale };
			double[] coordinate2 = { Double.parseDouble(errataLine[5]) / scale,
					Double.parseDouble(errataLine[6]) / scale };
			double[] coordinate3 = { Double.parseDouble(errataLine[7]) / scale,
					Double.parseDouble(errataLine[8]) / scale };
			double[] coordinate4 = { Double.parseDouble(errataLine[9]) / scale,
					Double.parseDouble(errataLine[10]) / scale };

			ArrayList<double[]> coordinates = new ArrayList<double[]>();
			coordinates.add(coordinate1);
			coordinates.add(coordinate2);
			coordinates.add(coordinate3);
			coordinates.add(coordinate4);

			// 内角
			angle0 = CVErrataGeometry.getInteriorAngle(coordinates, 0);
			angle1 = CVErrataGeometry.getInteriorAngle(coordinates, 1);
			angle2 = CVErrataGeometry.getInteriorAngle(coordinates, 2);
			angle3 = CVErrataGeometry.getInteriorAngle(coordinates, 3);

			// 周囲
			perimeter = CVErrataGeometry.getPerimeter(coordinates);

			// 内積
			inner0 = CVErrataGeometry.getInner(coordinates, 0);
			inner1 = CVErrataGeometry.getInner(coordinates, 1);

			// 長さ比
			norm0 = CVErrataGeometry.getNormRatioSideBySide(coordinates, 0);
			norm1 = CVErrataGeometry.getNormRatioSideBySide(coordinates, 1);
			// アスペクト比
			aspect = CVErrataGeometry.getAspectRatio(coordinates);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(angle0);
		sb.append("\t");
		sb.append(angle1);
		sb.append("\t");
		sb.append(angle2);
		sb.append("\t");
		sb.append(angle3);
		sb.append("\t");
		sb.append(perimeter);
		sb.append("\t");
		sb.append(inner0);
		sb.append("\t");
		sb.append(inner1);
		sb.append("\t");
		sb.append(norm0);
		sb.append("\t");
		sb.append(norm1);
		sb.append("\t");
		sb.append(aspect);
		return sb.toString();
	}

	/**
	 * 座標情報の共分散行列の固有値を返す
	 * 
	 * @param coordinates
	 * @return null
	 */
	public static double getCoordinateVarianceCovarianceMatrix(ArrayList<double[]> coordinates) {
		// 座標情報が無い場合は返す
		if (coordinates.size() == 0) {
			return Double.NaN;
		}
		int num = coordinates.size();

		// 座標情報の平均
		double sumx = 0;
		double sumy = 0;
		for (double[] coordinate : coordinates) {
			sumx += coordinate[0];
			sumy += coordinate[1];
		}
		double mx = sumx / (double) num;
		double my = sumy / (double) num;

		// 標準偏差
		double gapx = 0;
		double gapy = 0;
		for (double[] coordinate : coordinates) {
			gapx += Math.pow(coordinate[0] - mx, 2);
			gapy += Math.pow(coordinate[1] - my, 2);
		}
		double deltax = Math.sqrt(gapx / (double) num);
		double deltay = Math.sqrt(gapy / (double) num);

		// 正規化行列の部品
		// vector matの [0][0], [1][0] (x方向の正規化)
		double[] vector0 = { 1 / deltax, 0 };
		// vector matの [1][0], [1][1] (y方向の正規化)
		double[] vector1 = { 0, 1 / deltay };

		// 正規化行列
		// mat [0][0], [1][0]
		// mat [0][1], [1][1]
		// 成分を使すればOK
		// double[][] mat = { vector0, vector1 };

		// 正規化
		ArrayList<double[]> normalizeCoordinates = new ArrayList<>();
		for (double[] coordinate : coordinates) {
			double normalizeX = vector0[0] * coordinate[0] + vector0[1] * coordinate[1];
			double normalizeY = vector1[0] * coordinate[0] + vector1[1] * coordinate[1];

			double[] normalizeCoordinate = { normalizeX, normalizeY };
			normalizeCoordinates.add(normalizeCoordinate);
		}

		double nsumx = 0;
		double nsumy = 0;
		for (double[] normalizeCoordinate : normalizeCoordinates) {
			nsumx += normalizeCoordinate[0];
			nsumy += normalizeCoordinate[1];
		}
		double nmx = nsumx / (double) num;
		double nmy = nsumy / (double) num;

		// 共分散行列
		double tmpConvXX = 0;
		double tmpConvXY_YX = 0;
		double tmpConvYY = 0;
		for (double[] normalizeCoordinate : normalizeCoordinates) {
			tmpConvXX += Math.pow(normalizeCoordinate[0] - nmx, 2);
			tmpConvXY_YX += (normalizeCoordinate[0] - nmx) * (normalizeCoordinate[1] - nmy);
			tmpConvYY += Math.pow(normalizeCoordinate[1] - nmy, 2);
		}
		double convXX = tmpConvXX / (double) num;
		double convXY_YX = tmpConvXY_YX / (double) num;
		double convYY = tmpConvYY / (double) num;

		// 共分散行列の固有方程式
		// ax^2 + bx + c = 0
		// x^2 + (-convYY-convXX)x + convXX*convYY - convXY*convYX = 0
		double a = 1;
		double b = -convYY - convXX;
		double c = convXX * convYY - Math.pow(convXY_YX, 2);
		double root1 = (-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a);
		double root2 = (-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a);

		if (root1 > root2) {
			return root1;
		} else {
			return root2;
		}
	}

	/**
	 * 座標情報の分散を返す
	 * 
	 * @param coordinates
	 * @return sigma xとy
	 */
	public static double[] getCoordinateSigma(ArrayList<double[]> coordinates) {
		// 座標情報が無い場合は返す
		if (coordinates.size() == 0) {
			double[] nan = { Double.NaN, Double.NaN };
			return nan;
		}
		int num = coordinates.size();

		// 座標情報の平均
		double sumx = 0;
		double sumy = 0;
		for (double[] coordinate : coordinates) {
			sumx += coordinate[0];
			sumy += coordinate[1];
		}
		double mx = sumx / (double) num;
		double my = sumy / (double) num;

		// 標準偏差
		double sigmax = 0;
		double sigmay = 0;
		for (double[] coordinate : coordinates) {
			sigmax += Math.pow(coordinate[0] - mx, 2);
			sigmay += Math.pow(coordinate[1] - my, 2);
		}

		double deltax = Math.sqrt(sigmax / num);
		double deltay = Math.sqrt(sigmay / num);

		double[] delta = { deltax, deltay };

		// 計算量が大きすぎるので標準偏差に変更して実験
		return delta;
	}
}
