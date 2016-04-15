package jp.sprix.cv;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import jp.sprix.io.Import;
import jp.sprix.io.Output;
import jp.sprix.learning.data.ImageDataArrange;

/**
 * 訓練画像クラス
 * 
 * @author root
 * 
 */
public class TrainImage {
	/**
	 * 指定したディレクトリにある訓練画像の中で、規格外の画像を検出する
	 * 
	 * @param TrainImageDir
	 */
	public static void extractionNostandardized(String TrainImageDir) {
		ImageDataArrange imageDataArrange = new ImageDataArrange();
		imageDataArrange.outputTrainImageFilePath(TrainImageDir);
		Properties prop = Import.importProperty();
		String outputFile = prop.getProperty("file.size.output");
		StringBuilder sb = new StringBuilder();

		ArrayList<String> imagePathList = imageDataArrange.getImagePathList();
		for (String imagePath : imagePathList) {
			Image image = CVImage.loadImage(new File(imagePath));
			if (image == null) {
				System.out.println("[error] can not load image. " + imagePath);
				continue;
			}
			int height = image.getHeight(null);
			int width = image.getWidth(null);
			System.out.println(imagePath + "\t" + width + "\t" + height);
			if (width != 3000 || height != 4512) {
				sb.append(imagePath);
				sb.append("\t");
				sb.append(width);
				sb.append("\t");
				sb.append(height);
				sb.append("\n");
			}

			// debug
			// break;
		}
		Output.outputFile(outputFile, sb);
	}

	public static void dupulicateImage(String TrainImageDir) {
		ImageDataArrange imageDataArrange = new ImageDataArrange();
		imageDataArrange.outputTrainImageFilePath(TrainImageDir);
		Properties prop = Import.importProperty();
		String outputFile = prop.getProperty("dupulicate.file.name");
		StringBuilder sb = new StringBuilder();
		HashMap<String, String> duplicateMap = new HashMap<String, String>();

		ArrayList<String> imagePathList = imageDataArrange.getImagePathList();
		for (String imagePath : imagePathList) {
			File file = new File(imagePath);
			String fileName = file.getName();

			if (!duplicateMap.containsKey(fileName)) {
				duplicateMap.put(file.getName(), file.getPath());
				// sb.append(fileName);
				// sb.append("\n");
			} else {
				String duplicatePath = duplicateMap.get(fileName);
				sb.append(fileName);
				sb.append("\t");
				sb.append("duplicate");
				sb.append("\t");
				sb.append(duplicatePath);
				sb.append("\t");
				sb.append(file.getPath());
				sb.append("\n");
			}

			// debug
			// break;
		}
		Output.outputFile(outputFile, sb);
	}
}
