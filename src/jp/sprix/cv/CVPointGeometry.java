package jp.sprix.cv;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class CVPointGeometry {
	/**
	 * 指定した2点間(Point2D.Doubl)の距離を返す
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static double getDistance(Point2D.Double from, Point2D.Double to) {
		double x = from.x - to.x;
		double y = from.y - to.y;
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	/**
	 * 指定した2点間(Point)の距離を返す
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static double getDistanceInt(Point from, Point to) {
		double x = from.getX() - to.getX();
		double y = from.getY() - to.getY();
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	/**
	 * errata行からpoint配列を返す
	 * 
	 * @param errataLine
	 * @return Point2D.Double[]
	 */
	public static Point2D.Double[] getPointsFromErrata(String[] errataLine) {
		Point2D.Double[] points = new Point2D.Double[4];
		for (int i = 0; i < points.length; i++) {
			points[i] = new Point2D.Double(Double.parseDouble(errataLine[(2 * i) + 3]),
					Double.parseDouble(errataLine[(2 * i) + 4]));
			// points[i] = new Point2D.Double(Double.parseDouble(errataLine[(2 *
			// i) + 3]),
			// Double.parseDouble(errataLine[(2 * i) + 4]));
		}
		return points;
	}

	/**
	 * 指定したindexの内角を取得
	 * 
	 * @param points
	 * @param index
	 * @return
	 */
	public static double getInteriorAngle(Point2D.Double[] points, int index) {
		if (points == null) {
			return 0;
		}
		if (points.length < 4) {
			return 0;
		}

		// 指定点
		int count = points.length;
		// 点の最大値
		int maxNum = count - 1;
		if (index < 0 || maxNum < index) {
			// 指定された頂点の番号が不正
			return 0;
		}

		Point2D.Double angleCoordinate = points[index];
		double centerX = angleCoordinate.getX();
		double centerY = angleCoordinate.getY();

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
		Point2D.Double previousCoordinate = points[previousIndex];
		double previousX = previousCoordinate.getX();
		double previousY = previousCoordinate.getY();
		Point2D.Double nextCoordinate = points[nextIndex];
		double nextX = nextCoordinate.getX();
		double nextY = nextCoordinate.getY();

		// ベクトル
		double vPreCenX = previousX - centerX;
		double vPreCenY = previousY - centerY;
		double vNextCenX = nextX - centerX;
		double vNextCenY = nextY - centerY;

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
	 * 周囲を取得
	 * 
	 * @param points
	 * @return
	 */
	public static double getPerimeter(Point2D.Double[] points) {
		if (points == null) {
			return 0;
		}
		if (points.length < 4) {
			return 0;
		}

		// 頂点数
		int count = points.length;

		// 周囲長
		double length = 0;
		for (int i = 0; i < count; i++) {
			length += getDistanceToNext(i, points);
		}
		return length;
	}

	/**
	 * 指定した座評点の次の点までの距離を取得
	 * 
	 * @param index
	 * @param points
	 * @return
	 */
	public static double getDistanceToNext(int index, Point2D.Double[] points) {
		if (points.length < 0) {
			return 0;
		}

		// 指定した番号
		int count = points.length;
		int maxCoordinateNum = count - 1;
		if (index < 0 || maxCoordinateNum < index) {
			// 指定された頂点の番号が不正
			return 0;
		}
		int nextIndex = (index + 1) % count;

		double iniPointX = points[index].getX();
		double iniPointY = points[index].getY();

		double termPointX = points[nextIndex].getX();
		double termPointY = points[nextIndex].getY();

		return Math.sqrt(Math.pow(termPointX - iniPointX, 2) + Math.pow(termPointY - iniPointY, 2));
	}

	/**
	 * 内角を取得
	 * 
	 * @param points
	 * @param index
	 * @return
	 */
	public static double getInner(Point2D.Double[] points, int index) {
		if (points.length < 0) {
			return 0;
		}

		int count = points.length;
		int max_index = count - 1;
		if (index < 0 || max_index < index) {
			// 指定された頂点の番号が不正
			return 0;
		}
		int nextIndex = (index + 1) % count;

		double iniPointX = points[index].getX();
		double iniPointY = points[index].getY();

		double termPointX = points[nextIndex].getX();
		double termPointY = points[nextIndex].getY();

		double vectorX = termPointX - iniPointX;
		double vectorY = termPointY - iniPointY;

		// 隣合わない辺
		int oppIndex = (index + 2) % count;
		int termOppIndex = (oppIndex + 1) % count;

		double iniOppPointX = points[oppIndex].getX();
		double iniOppPointY = points[oppIndex].getY();

		double iniTermOppPointX = points[termOppIndex].getX();
		double iniTremOppPointY = points[termOppIndex].getY();

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
	public static double getNormRatioSideBySide(Point2D.Double[] points, int index) {
		if (points.length < 0) {
			return 0;
		}
		int count = points.length;
		int max_index = count - 1;
		if (index < 0 || max_index < index) {
			// 指定された頂点の番号が不正
			return 0;
		}
		double distance_ini = getDistanceToNext(index, points);

		// 隣り合わない辺の始点
		int oppIndex = (index + 2) % count;
		double distance_opp = getDistanceToNext(oppIndex, points);

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
	public static double getAspectRatio(Point2D.Double[] points) {
		if (points == null) {
			return 0;
		}
		if (points.length < 4) {
			return 0;
		}

		double width = getDistanceToNext(0, points) + getDistanceToNext(2, points);
		double height = getDistanceToNext(1, points) + getDistanceToNext(3, points);

		if (height == 0) {
			return 0;
		}

		return width / height;
	}

	/**
	 * 座標情報の共分散行列の固有値を返す
	 * 
	 * @param points
	 * 
	 * @return double 共分散行列の固有値
	 */
	public static double getCoordinateVarianceCovarianceMatrix(Point2D.Double[] points) {
		ArrayList<double[]> coordinates = new ArrayList<>();

		for (Point2D.Double point : points) {
			double[] coordinate = { point.getX(), point.getY() };
			coordinates.add(coordinate);
		}

		return CVErrataGeometry.getCoordinateVarianceCovarianceMatrix(coordinates);

	}

	/**
	 * 座標情報のx及びy方向の分散を返す
	 * 
	 * @param points
	 * @return double[x方向の分散、y方向の分散]
	 */
	public static double[] getCoordinateSigma(Point2D.Double[] points) {
		ArrayList<double[]> coordinates = new ArrayList<>();

		for (Point2D.Double point : points) {
			double[] coordinate = { point.getX(), point.getY() };
			coordinates.add(coordinate);
		}

		return CVErrataGeometry.getCoordinateSigma(coordinates);
	}
}
