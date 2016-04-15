package jp.sprix.io;

public class Utils {
	/**
	 * 配列の結合
	 * 
	 * @param arry
	 * @param with
	 * @return
	 */
	public static String join(String[] arry, String with) {
		StringBuffer buf = new StringBuffer();
		for (String s : arry) {
			if (buf.length() > 0) {
				buf.append(with);
			}
			buf.append(s);
		}
		return buf.toString();
	}

	/**
	 * 配列の結合
	 * 
	 * @param arry
	 * @param with
	 * @return
	 */
	public static String joinInt(int[] arry, String with) {
		StringBuffer buf = new StringBuffer();
		for (int s : arry) {
			if (buf.length() > 0) {
				buf.append(with);
			}
			buf.append(Integer.toString(s));
		}
		return buf.toString();
	}
}
