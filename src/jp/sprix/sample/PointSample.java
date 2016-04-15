package jp.sprix.sample;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * サンプルを取得するクラス
 * 
 * @author root
 * 
 */
public class PointSample {
	/**
	 * Point2D.Double型の
	 * 
	 * @param cnt
	 *            point's cnt
	 * @return Point2D.Double random number in 1~max
	 */
	public static Point2D.Double getRandomPointDoubleSample(int max) {
		// Random
		Point2D.Double point = new Point2D.Double();
		double random_x = (double) (Math.random() * max);
		double random_y = (double) (Math.random() * max);
		point.setLocation(random_x, random_y);
		System.out.println(random_x + ", " + random_y);
		return point;
	}

	public static Point getRandomPointSample(int max) {
		// Random
		Point point = new Point();
		int random_x = (int) (Math.random() * max);
		int random_y = (int) (Math.random() * max);
		point.setLocation(random_x, random_y);
		System.out.println(random_x + ", " + random_y);
		return point;
	}

	public static int getRandomInt(int max) {
		return (int) (Math.random() * max);
	}
}
