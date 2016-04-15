package jp.sprix.learning.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ClassifyImageFile {
	/**
	 * 指定したイメージディレクトリ配下にある画像ファイルを、クラスリストファイル中のカテゴリーのディレクトリへ移動する。
	 * 
	 * @param classListFilePath
	 *            クラスリストファイルのパス
	 * @param imageDirPath
	 *            画像ファイルのあるディレクトリのパス
	 * @param type
	 *            画像のタイプ train or query (ルートディレクトリ/train or queryに画像を保存)
	 */
	public static void classifyImage(String classListFilePath, String imageDirPath, String type) {
		File file = new File(classListFilePath);
		File imageDir = new File(imageDirPath);
		File[] imageFiles = imageDir.listFiles();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				for (File f : imageFiles) {
					String imageFileName = f.getName();
					if (imageFileName.matches(line + ".*")) {
						f.renameTo(new File("/media/sf_images/" + type + "/" + line + "/",
								imageFileName));
					}
				}
			}
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
