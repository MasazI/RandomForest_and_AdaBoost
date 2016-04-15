package jp.sprix.learning.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import jp.sprix.io.Output;

/**
 * 指定したファイルを行単位に包含をチェックする
 * 
 * @author root
 * 
 */
public class FileLineDifference {
	/**
	 * 2つのファイルの差分を出力する
	 * 
	 * 大小文字を無視する
	 * 
	 * @param path1
	 * @param path2
	 */
	public static void difference(String path1, String path2) {
		String outputFilePath1 = "/data/output/diff_1_only.tsv";
		String outputFilePath2 = "/data/output/diff_2_only.tsv";
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		File file1 = new File(path1);
		if (!file1.exists()) {
			System.out.println("[error] can not load file. " + path1);
			return;
		}
		ArrayList<String> fileLines1 = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file1));
			String line1 = "";
			while ((line1 = br.readLine()) != null) {
				fileLines1.add(line1.toUpperCase());
			}
			// バッファのクローズ
			br.close();
		} catch (Exception e) {
			System.out.println("[error] errata file." + path1 + ". " + e.getMessage());
		}
		System.out.println("path1 size: " + path1 + ":" + fileLines1.size());

		File file2 = new File(path2);
		if (!file2.exists()) {
			System.out.println("[error] can not load file. " + path1);
			return;
		}
		ArrayList<String> fileLines2 = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file2));
			String line2 = "";
			while ((line2 = br.readLine()) != null) {
				fileLines2.add(line2.toUpperCase());
			}
			// バッファのクローズ
			br.close();
		} catch (Exception e) {
			System.out.println("[error] errata file." + path2 + ". " + e.getMessage());
		}
		System.out.println("path2 size: " + path2 + ":" + fileLines2.size());

		for (String line : fileLines1) {
			if (!fileLines2.contains(line)) {
				System.out.println(path1 + "only include " + line);
				sb1.append(line);
				sb1.append("\n");
			}
		}
		Output.outputFile(outputFilePath1, sb1);

		for (String line : fileLines2) {
			if (!fileLines1.contains(line)) {
				System.out.println(line);
				System.out.println(path2 + " include " + line);
				sb2.append(line);
				sb2.append("\n");
			}
		}
		Output.outputFile(outputFilePath2, sb2);

	}
}
