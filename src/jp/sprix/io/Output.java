package jp.sprix.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import jp.sprix.learning.report.ReportData;
import jp.sprix.threshold.CoordinatesThreshold;

public class Output {
	/**
	 * 指定されたStringBuilderの内容をファイルに書き出す
	 * 
	 * @param sb
	 */
	public static void outputFile(String filePath, StringBuilder sb) {
		try {
			FileWriter fw = new FileWriter(filePath, false);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			pw.print(sb.toString());
			pw.close();
		} catch (IOException e) {
			System.out.println("[error]" + filePath + ", " + e.getMessage());
		}
		System.out.println("[file output finish]" + filePath);
	}

	/**
	 * fileWriterを指定して文字列をファイルに書き込む
	 * 
	 * @param sb
	 */
	public static void outputFileWriter(FileWriter fw, String str) {
		PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
		pw.println(str);
		pw.close();
	}

	/**
	 * 指定されたStringBuilderの内容をファイルに書き出す
	 * 
	 * @param sb
	 */
	public static void outputFile(String filePath, StringBuffer sb) {
		try {
			FileWriter fw = new FileWriter(filePath, false);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			pw.print(sb.toString());
			pw.close();
		} catch (IOException e) {
			System.out.println("[error]" + filePath + ", " + e.getMessage());
		}
		System.out.println("[file output finish]" + filePath);
	}

	/**
	 * タイプとレポート
	 * 
	 * @param type
	 * @param threshold
	 * @param reportData
	 */
	public static void outputReportDataLine(String type, CoordinatesThreshold threshold,
			ReportData reportData, StringBuilder sb) {
		System.out.print(type);
		sb.append(type);
		System.out.print("\t");
		sb.append("\t");
		System.out.print(threshold.output());
		sb.append(threshold.output());
		System.out.print("\t");
		sb.append("\t");
		System.out.println(reportData.output());
		sb.append(reportData.output());
		sb.append("\n");
	}
}
