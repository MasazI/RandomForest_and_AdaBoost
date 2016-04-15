package jp.sprix.learning.post;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseFromKurusugawa {
	public static String outputResponse(String data, String path) {
		// エンジンからの戻りを記録するファイル
		String outputFilePath = path;
		File outputFile = new File(outputFilePath);
		PrintWriter printWriter = null;
		String[] dataArray = data.split("\n");
		try {
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
			printWriter.println(dataArray[0]);
			System.out.println(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		printWriter.close();
		return outputFilePath;
	}
}
