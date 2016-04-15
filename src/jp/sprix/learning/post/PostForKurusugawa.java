package jp.sprix.learning.post;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jp.sprix.executer.ShellCommandExecuter;
import jp.sprix.learning.ImageListData;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

//import com.sun.org.apache.xalan.internal.xsltc.trax.*;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class PostForKurusugawa {
	Properties prop = null;

	/**
	 * コンストラクタ
	 * 
	 * @param prop
	 */
	public PostForKurusugawa(Properties mProp) {
		prop = mProp;
	}

	/**
	 * 質問画像をエンジンにポストするメソッド
	 * 
	 * @param data
	 *            image's data for post
	 * @return ResponseFromKurusugawa's data
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws FileNotFoundException
	 */
	public HashMap<String, String[]> postImageData(ImageListData imageListData)
			throws ParserConfigurationException, TransformerException, FileNotFoundException {
		HashMap<String, String[]> responseCategoryMap = imageToBinary(imageListData);
		// レスポンス結果ファイルの名前を返す
		return responseCategoryMap;
	}

	/**
	 * 画像データ(バイナリ)とクエリ文字列で送信する
	 * 
	 * @param imageListData
	 * @return
	 */
	public HashMap<String, String[]> imageToBinaryQueryStrings(ImageListData imageListData,
			int dicSize) {
		HashMap<String, String[]> fileNameMap = new HashMap<String, String[]>();

		// レスポンスファイル名に使用
		// 質問ファイルのパスとNoの対応Mapを取得。keyがNo、valueがパス
		HashMap<String, String> imageNameMap = imageListData.getImageFilePathMap();
		// 質問ファイルのNoと正解カテゴリーのMapを取得
		HashMap<String, String> answerMap = imageListData.getAnswerMap();

		int cnt = 1;
		for (Map.Entry<String, String> e : imageNameMap.entrySet()) {
			// 処理ファイルを出力し、ファイルのパスからファイルオブジェクトを作成
			String imageFilePath = e.getKey();
			File file = new File(imageFilePath);

			if (!file.exists()) {
				System.out.println("fileが存在しません。" + file.getPath());
				return null;
			}

			// dicSize分回す（精度測定ツールは直列）
			for (int i = 1; i <= dicSize; i++) {
				// バイナリで送信する
				String response = postImageDataBinary(file, i);

				// サーバからのレスポンスを永続化する
				String response_file = ResponseFromKurusugawa.outputResponse(response,
						(String) prop.getProperty("response.xml.path") + "/" + e.getValue() + "_"
								+ i);
				// keyはレスポンスファイルのパス。valueは{質問ファイル名, 正解カテゴリー}
				String[] question_category = { file.getName(), answerMap.get(e.getValue()) };
				fileNameMap.put(response_file, question_category);
			}
			cnt++;
		}

		System.out.println("総計: " + cnt);
		return fileNameMap;
	}

	/**
	 * 画像データをポスト用にバイナリ化してサーバへ送信する
	 * 
	 * @param imageListData
	 * 
	 * @return ArrayList<String>
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws FileNotFoundException
	 */
	private HashMap<String, String[]> imageToBinary(ImageListData imageListData)
			throws ParserConfigurationException, TransformerException, FileNotFoundException {
		HashMap<String, String[]> fileNameMap = new HashMap<String, String[]>();

		// 質問ファイルのパスとNoの対応Mapを取得。keyがNo、valueがパス
		HashMap<String, String> imageNameMap = imageListData.getImageFilePathMap();
		// 質問ファイルのNoと正解カテゴリーのMapを取得
		HashMap<String, String> answerMap = imageListData.getAnswerMap();

		// 質問ファイル分ループする
		int cnt = 1;
		for (Map.Entry<String, String> e : imageNameMap.entrySet()) {
			// 処理ファイルを出力し、ファイルのパスからファイルオブジェクトを作成
			File file = new File(e.getKey());

			if (!file.exists()) {
				System.out.println("fileが存在しません。" + file.getPath());
				return null;
			}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.newDocument();

			Element request = document.createElement("Request");
			document.appendChild(request);

			// key
			Element key = document.createElement("AccessKey");
			request.appendChild(key);
			Text keyStr = document.createTextNode("qlwdTVKHRmpVRIS5");
			key.appendChild(keyStr);

			// candidates
			Element candidates = document.createElement("NumberOfCandidates");
			request.appendChild(candidates);
			Text candidatesStr = document.createTextNode(imageListData.getCandidates());
			candidates.appendChild(candidatesStr);

			// image
			Element imageData = document.createElement("ImageData");
			request.appendChild(imageData);
			Text imageDataStr = document.createCDATASection(imageTobase64(file));
			imageData.appendChild(imageDataStr);

			TransformerFactory tf = TransformerFactoryImpl.newInstance();
			tf.setAttribute(TransformerFactoryImpl.INDENT_NUMBER, "2");

			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");

			// 保存するファイル名
			String fileName = (String) prop.getProperty("query.xml.path") + "/" + e.getValue();
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			DOMSource source = new DOMSource(document);
			transformer.transform(source, result);
			File newXML = new File(fileName);
			FileOutputStream os = new FileOutputStream(newXML);
			StreamResult xmlResult = new StreamResult(os);
			transformer.transform(source, xmlResult);

			// 標準出力
			System.out.print(cnt + "個目\t");
			System.out.print(e.getKey() + "\t");
			// サーバへリクエストを投げる(java版)
			String response = postImageData(writer.toString());
			// サーバへリクエストを投げる(curl版)
			// String response = postImageDataExec(fileName);

			// サーバからのレスポンスを永続化する
			String response_file = ResponseFromKurusugawa.outputResponse(response,
					(String) prop.getProperty("response.xml.path") + "/" + e.getValue());
			// keyはレスポンスファイルのパス。valueは{質問ファイル名, 正解カテゴリー}
			String[] question_category = { file.getName(), answerMap.get(e.getValue()) };
			fileNameMap.put(response_file, question_category);

			// テスト用
			// break;

			// カウントアップ
			cnt++;
		}
		System.out.println("総計: " + cnt);
		return fileNameMap;
	}

	public String postImageDataExec(String fileName) {
		ShellCommandExecuter sce = new ShellCommandExecuter();
		String result = null;
		String url = "http://10.0.50.224:8080/imagefindersv/xml/search";
		String data = "@" + fileName;
		try {
			result = sce.doExec(new String[] {
					"/bin/sh",
					"-c",
					"/usr/bin/curl -k " + url + " --data-binary " + data
							+ " -H \"Content-Type: text/xml; charset=utf-8\"" });

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 画像ファイルをバイナリ送信する
	 * 
	 * @param imageFilePath
	 * @return
	 */
	public static String postImageDataBinary(File imageFile, int dicId) {
		String response = null;
		String errorMessage = null;

		HttpURLConnection conn = null;
		InputStream is = null;
		BufferedReader reader = null;

		try {
			// クエリ文字列を作成する
			String queryString = "http://localhost:8080/imagefindersv/xml/" + dicId
					+ "/search?NumOfCandidates=10";

			URL url = new URL(queryString);
			// connection
			conn = (HttpURLConnection) url.openConnection();
			// POST
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			// content-type
			conn.setRequestProperty("Content-Type", "application/octet-stream");
			// no follow redirect
			HttpURLConnection.setFollowRedirects(false);
			conn.setInstanceFollowRedirects(false);

			// fileのbyte配列を流す
			byte[] binary = imageToBinary(imageFile);

			OutputStream out = conn.getOutputStream();
			out.write(binary);
			out.close();

			// input stream
			is = conn.getInputStream();
			// reader for response
			reader = new BufferedReader(new InputStreamReader(is));
			// get response
			String s;
			response = "";
			while ((s = reader.readLine()) != null)
				response += s;
			// close
			reader.close();
			reader = null;
			is.close();
			is = null;

			// disconnect
			conn.disconnect();
			conn = null;

		} catch (Exception e) {
			errorMessage = "message: " + e.getMessage() + ", cause: " + e.getCause();
			return errorMessage;
		}
		return response;

	}

	public static String postImageData(String request) {
		String response = null;
		String errorMessage = null;

		HttpURLConnection conn = null;
		OutputStreamWriter osw = null;
		InputStream is = null;
		BufferedReader reader = null;

		try {
			URL url = new URL("http://localhost:8080/imagefindersv/xml/search");
			// connection
			conn = (HttpURLConnection) url.openConnection();
			// POST
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			// content-type
			conn.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
			// no follow redirect
			HttpURLConnection.setFollowRedirects(false);
			conn.setInstanceFollowRedirects(false);

			// stream
			osw = new OutputStreamWriter(conn.getOutputStream());
			if (request.length() > 0) {
				osw.write(request);
				osw.flush();
				osw.close();
				osw = null;
			}

			// input stream
			is = conn.getInputStream();
			// reader for response
			reader = new BufferedReader(new InputStreamReader(is));
			// get response
			String s;
			response = "";
			while ((s = reader.readLine()) != null)
				response += s;
			// close
			reader.close();
			reader = null;
			is.close();
			is = null;

			// disconnect
			conn.disconnect();
			conn = null;

		} catch (Exception e) {
			errorMessage = "message: " + e.getMessage() + ", cause: " + e.getCause();
			return errorMessage;
		}
		return response;
	}

	/**
	 * 画像のバイト配列を作成する
	 * 
	 * @param imageFile
	 * @return
	 */
	private static byte[] imageToBinary(File imageFile) {
		BufferedImage image = null;

		try {
			image = ImageIO.read(imageFile);
		} catch (IOException e) {
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		image.flush();
		try {
			ImageIO.write(image, "jpg", bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
		}

		byte[] bImage = baos.toByteArray();

		return bImage;
	}

	/**
	 * 
	 * @param imageFile
	 * @return
	 */
	private static String imageTobase64(File imageFile) {
		BufferedImage image = null;

		try {
			image = ImageIO.read(imageFile);
		} catch (IOException e) {
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		image.flush();
		try {
			ImageIO.write(image, "jpg", bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
		}

		byte[] bImage = baos.toByteArray();

		// System.out.println(Base64.encode(bImage));

		return Base64.encode(bImage);
	}
}
