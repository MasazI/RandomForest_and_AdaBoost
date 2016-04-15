package jp.sprix.randomforest.errata;

/**
 * 分離関数候補クラス
 * 
 * @author root
 * 
 */
public class NodeType {
	private static final String ANGLE0 = "angle0";

	private static final String ANGLE1 = "angle1";

	private static final String ANGLE2 = "angle2";

	private static final String ANGLE3 = "angle3";

	private static final String PERIMETER = "perimeter";

	private static final String INNER0 = "inner0";

	private static final String INNER1 = "inner1";

	private static final String SCORE = "score";

	private static final String NORMRATIO0 = "normratio0";

	private static final String NORMRATIO1 = "normratio1";

	private static final String ASPECTRATIO = "aspectratio";

	private static final String POINT0X = "point0x";

	private static final String POINT1X = "point1x";

	private static final String POINT2X = "point2x";

	private static final String POINT3X = "point3x";

	private static final String POINT0Y = "point0y";

	private static final String POINT1Y = "point1y";

	private static final String POINT2Y = "point2y";

	private static final String POINT3Y = "point3y";

	// 特徴量追加
	private static final String NEW = "new";

	// 特徴量
	public static final String[] nodeTypes = { ANGLE0, ANGLE1, ANGLE2, ANGLE3, PERIMETER, INNER0,
			INNER1, SCORE, NORMRATIO0, NORMRATIO1, ASPECTRATIO, POINT0X, POINT0Y, POINT1X, POINT1Y,
			POINT2X, POINT2Y, POINT3X, POINT3Y };

	// public static final String[] nodeTypes = { XLINE, YLINE };
}
