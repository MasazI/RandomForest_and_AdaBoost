package jp.sprix.randomforest;

/**
 * 分離関数候補クラス
 * 
 * @author root
 * 
 */
public class NodeType {
	private static final String XLINE = "x";

	private static final String YLINE = "y";

	private static final String D1 = "distanse1";

	private static final String D2 = "distanse2";

	// 特徴量追加
	private static final String D12 = "distanse12";

	// 一時
	public static final String[] nodeTypes = { XLINE, YLINE, D1, D2, D12 };

	//public static final String[] nodeTypes = { XLINE, YLINE };
}
