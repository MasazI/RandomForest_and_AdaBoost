package jp.sprix.learning.evaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jp.sprix.learning.ImageListData;

public class EvaluationKurusugawa {
	private static final String ERRATA_DIR = "output.all.errata.path";

	// 廃止
	// private static final String RESULT = "result";

	private static final String CANDIDATES = "Candidates";

	private static final String LABEL = "Label";

	private static final String MAPPING = "Mapping";

	private static final String LABEL_SOURCE = "LabelSource";

	// 廃止
	// private static final String SCORE = "Score";

	private static FileWriter filewriter;

	/**
	 * レスポンスファイルの解析
	 * 
	 * @param prop
	 *            プロパティ
	 * @param responseFileMap
	 *            レスポンスファイルのMap. keyはレスポンスファイルのパス。valueは{質問ファイル名, 正解カテゴリー}
	 * @param rank
	 *            何位までをerrataファイルに含めるかどうか
	 * @throws IOException
	 */
	public static void evaluation(Properties prop, HashMap<String, String[]> responseFileMap,
			int rank) throws IOException {

		String learningType = (String) prop.get("learningType");
		String errataFilePath = null;
		if (learningType.equals("train")) {
			errataFilePath = (String) prop.get(ERRATA_DIR) + "errata_20130528" + rank;
		} else if (learningType.equals("all")) {
			errataFilePath = (String) prop.get(ERRATA_DIR) + "errata_20130528" + rank;
		} else if (learningType.equals("part")) {
			errataFilePath = (String) prop.get(ERRATA_DIR) + "errata_20130528" + rank;
		} else {
			return;
		}

		// 質問ファイル名とNoのマップ値
		String[] fileNameParts = prop.getProperty("imagelist.all.path").split("/");
		errataFilePath += fileNameParts[fileNameParts.length - 1];
		File errataFile = new File(errataFilePath);
		filewriter = new FileWriter(errataFile, true);

		for (Map.Entry<String, String[]> e : responseFileMap.entrySet()) {
			
			// 質問ファイルに対してエンジンの数だけループする
			
			String fileName = e.getKey();
			String[] questionFileName_category = e.getValue();
			File file = new File(fileName);
			String result = parseXml(questionFileName_category, file, rank);
			if (result == null) {
				result = fileName + "\tno response\n";
			}
			try {
				filewriter.write(result);
			} catch (IOException ex) {
				ex.printStackTrace();
				continue;
			}
		}
		filewriter.close();
		System.out.println("errata file: " + errataFilePath);
	}

	/**
	 * xmlをパースする
	 * 
	 * @param String
	 *            [] questionFileName_category 質問ファイル名とカテゴリーの配列
	 * @param file
	 *            import file
	 * @param int 何位までパースするか
	 */
	private static String parseXml(String[] questionFileName_category, File file, int rank) {
		if (!file.exists()) {
			return null;
		}

		// xmlをパースする
		System.out.println("parse\t" + file.getName());
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document doc;

		// boolean is_dummy = false;
		// if (imageFileName.contains("dummy")) {
		// is_dummy = true;
		// }
		// output String
		StringBuilder sb = new StringBuilder();
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.parse(file);

		} catch (ParserConfigurationException e) {
			System.out.println("[error]ParserConfigurationException. " + e.getMessage());
			return "<error>";
		} catch (IOException e) {
			System.out.println("[error]IOException. " + e.getMessage());
			return "<error>";
		} catch (SAXException e) {
			System.out.println("[error]SAXException. " + e.getMessage());
			return "<error>";
		}

		NodeList element = doc.getElementsByTagName(CANDIDATES);
		for (int i = 0; i < element.getLength(); i++) {
			NodeList candidate = element.item(i).getChildNodes();
			int parseRanking = 0;
			for (int j = 0; j < candidate.getLength(); j++) {
				String is_same_property = "-";
				sb.append(questionFileName_category[0]);
				sb.append("\t");
				sb.append(j);
				sb.append("\t");
				NodeList resultdata = candidate.item(j).getChildNodes();
				for (int k = 0; k < resultdata.getLength(); k++) {
					String text = resultdata.item(k).getNodeName();
					String value = resultdata.item(k).getTextContent().trim();

					if (text.equals(MAPPING)) {
						NamedNodeMap attributes = resultdata.item(k).getAttributes();
						if (attributes != null) {
							for (int l = 0; l < attributes.getLength(); l++) {
								Node attribute = attributes.item(l);
								String name = attribute.getNodeName();
								if (name.equals("x") || name.equals("y")) {
									String point_value = attribute.getNodeValue();
									sb.append(point_value);
									sb.append("\t");
								}
							}
						}
					} else if (text.equals(LABEL)) {
						sb.append(value);
						sb.append("\t");

						// LABELがカテゴリーと等しければ+を選択する
						if (value.equals(questionFileName_category[1])) {
							is_same_property = "+";
						}
					} else {
						if (!text.equals(LABEL_SOURCE)) {
							sb.append(value);
							sb.append("\t");
						}
					}

				}
				sb.append(is_same_property);
				sb.append("\n");
				parseRanking++;
				if (parseRanking >= rank) {
					break;
				}
			}
		}
		System.out.println("\t" + sb.toString());
		return sb.toString();
	}

	// 未使用
	private static boolean isSameProperty(ArrayList<String> query, ArrayList<String> train) {
		int query_index = 0;
		for (Iterator<String> iterator = query.iterator(); iterator.hasNext();) {
			String property = iterator.next();

			int train_index = train.indexOf(property);
			if (train_index != query_index) {
				return false;
			}
			query_index++;
		}
		return true;
	}

	// 未使用
	private static ArrayList<String> getQueryPropertyByFileName(String fileName) {
		String[] fileNameSplitByMinus = null;
		if (fileName.contains("_Try")) {
			fileNameSplitByMinus = fileName.split("_Try");
		} else if (fileName.contains("_B")) {
			fileNameSplitByMinus = fileName.split("_B");
		} else if (fileName.contains("_Ex")) {
			fileNameSplitByMinus = fileName.split("_Ex");
		} else {
			fileNameSplitByMinus = fileName.split("-shitsumon");
		}

		if (fileNameSplitByMinus == null) {
			return null;
		}

		// use only opening
		String propertyStr = fileNameSplitByMinus[0];
		if (propertyStr == null || propertyStr.length() == 0) {
			return null;
		}
		return getPropetyByFileName(propertyStr);

	}

	// 未使用
	private static ArrayList<String> getTrainPropertyByFileName(String fileName) {
		String propertyStr = null;
		if (fileName.contains("-photo")) {
			String[] fileNameSplitByMinus = fileName.split("-photo");
			propertyStr = fileNameSplitByMinus[0];
		} else if (fileName.contains(".JPG")) {
			String[] fileNameSplitByMinus = fileName.split(".JPG");
			propertyStr = fileNameSplitByMinus[0];
		} else if (fileName.contains(".jpg")) {
			String[] fileNameSplitByMinus = fileName.split(".jpg");
			propertyStr = fileNameSplitByMinus[0];
		}

		if (propertyStr == null || propertyStr.length() == 0) {
			return null;
		}
		return getPropetyByFileName(propertyStr);
	}

	private static ArrayList<String> getPropetyByFileName(String fileName) {
		ArrayList<String> propertyList = new ArrayList<String>();
		String[] propertyStrByUnderScore = fileName.split("_");
		for (String propety : propertyStrByUnderScore) {
			propertyList.add(propety);
		}
		return propertyList;
	}
}
