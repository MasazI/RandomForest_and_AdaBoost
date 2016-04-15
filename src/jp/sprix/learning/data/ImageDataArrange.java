package jp.sprix.learning.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.FilenameFilter;

import jp.sprix.io.Output;

/**
 * arrange train data
 * 
 * @author root
 * 
 */
public class ImageDataArrange {
	private PrintWriter imagePathWriter = null;

	private ArrayList<String> imagePathList = new ArrayList<String>();

	/**
	 * 教科書名を出力したいカテゴリーのリストを返す
	 * 
	 * @param categoryNamePath
	 *            category's name list file path
	 * @return file's name list
	 */
	public static ArrayList<String> getCategoryNameList(String categoryNamePath) {
		ArrayList<String> fileNameList = new ArrayList<String>();
		File file = new File(categoryNamePath);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				// split
				String[] data = line.split("\t");
				fileNameList.add(data[0]);
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
		return fileNameList;
	}

	/**
	 * カテゴリーのリストから、教科書名のリストを取得する
	 * 
	 * @param categoryNames
	 *            category's name list
	 * @param textListPath
	 *            file's name and text's name list file path
	 * @return text's name list
	 */
	public static ArrayList<String> getTextNameList(ArrayList<String> categoryNames,
			String textListPath) {
		ArrayList<String> textNameList = new ArrayList<String>();
		HashMap<String, String> textNameMap = new HashMap<String, String>();
		File file = new File(textListPath);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				// split
				String[] data = line.split("\t");
				textNameMap.put(data[0], data[1]);
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
		for (Iterator<String> iterator = categoryNames.iterator(); iterator.hasNext();) {
			String inputFileName = iterator.next();

			String textName = textNameMap.get(inputFileName);
			if (textName == null) {
				textName = "not matching\t" + inputFileName;
			}
			// ユニークにして出力
			if (!textNameList.contains(inputFileName + "\t" + textName)) {
				textNameList.add(inputFileName + "\t" + textName);
			}
		}
		return textNameList;
	}

	/**
	 * 
	 * @param textNameList
	 */
	public static void outputTextNameList(ArrayList<String> textNameList) {
		String outputFilePath = "/root/category_check/text_in_train.txt";
		File outputFile = new File(outputFilePath);
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (printWriter != null) {
			for (Iterator<String> iterator = textNameList.iterator(); iterator.hasNext();) {
				String textName = iterator.next();
				System.out.println(textName);
				printWriter.println(textName);
			}
			printWriter.close();
		} else {
			System.out.println("[error]can not output file");
		}
	}

	/**
	 * 指定したディレクトリのイメージファイル名のリストを出力する(回帰)
	 * 
	 * @param imageDirPath
	 *            ファイル名のリストを出力したいディレクトリ
	 */
	public boolean outputTrainImageFilePath(String imageDirPath) {
		File file = new File(imageDirPath);
		FileWriter fw = null;
		try {
			fw = new FileWriter("/data/output/image_path_list_failed.tsv", true);
		} catch (IOException e) {
			System.out.println("[Error] image path list failed: " + e.getMessage());
		}

		File[] listFiles = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				// ドットで始まるファイルは対象外
				if (name.startsWith(".")) {
					return false;
				}
				// クラスファイルは対象外
				if (name.endsWith(".class")) {
					return false;
				}
				// 対象要素の絶対パスを取得
				String absolutePath = dir.getAbsolutePath() + File.separator + name;
				if (new File(absolutePath).isFile()) {
					putImagePathList(absolutePath);
					return true;
				} else {
					// ディレクトリの場合、再び同一メソッドを呼出す。
					return outputTrainImageFilePath(absolutePath);
				}
			}
		});

		if (imagePathWriter == null) {
			String outputFilePath = "/data/output/image_path_list.tsv";
			File outputFile = new File(outputFilePath);

			try {
				imagePathWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (imagePathWriter != null) {
			for (File f : listFiles) {
				if (f.isFile()) {
					// ディレクトリ名とファイルの整合性を確認
					String fileName = f.getName();

					// 親ディレクトリを取得
					String directoryPath = f.getParent();
					String[] directoryArray = directoryPath.split(System
							.getProperty("file.separator"));
					String parentDirectory = directoryArray[directoryArray.length - 1];

					// ファイル名をパーツに分けて、テキスト名のみにする
					String[] fileNamePartsArray = fileName.split("_");
					String textStr = null;
					for (int i = 0; i < fileNamePartsArray.length - 1; i++) {
						if (i == 0) {
							textStr = fileNamePartsArray[i];
						} else {
							textStr += "_";
							textStr += fileNamePartsArray[i];
						}
					}

					// ディレクトリとファイルのテキスト名が異なる場合
					if (!parentDirectory.equals(textStr)) {
						Output.outputFileWriter(fw, parentDirectory + "\t" + textStr);
					}

					// カテゴリーを作成する
					String pageNum = fileNamePartsArray[fileNamePartsArray.length - 1];
					String[] pageNumParts = pageNum.split("[.]");
					String categoryName = textStr + "_" + pageNumParts[0];

					System.out.println(f.getAbsolutePath() + "\t" + categoryName);
					imagePathWriter.println(f.getAbsolutePath() + "\t" + categoryName);
				}
			}
		} else {
			System.out.println("[error]can not output image's path");
		}
		return true;
	}

	public void closeTrainImagePathWriter() {
		imagePathWriter.close();
		imagePathWriter = null;
	}

	public ArrayList<String> getImagePathList() {
		return imagePathList;
	}

	public void setImagePathList(ArrayList<String> imagePathList) {
		this.imagePathList = imagePathList;
	}

	public void putImagePathList(String imagePath) {
		imagePathList.add(imagePath);
	}

	/**
	 * 訓練画像を持たないファイルを質問画像から除外したファイルを出力する
	 * 
	 * @param queryListPath
	 * @param trainCategoryFilePath
	 */
	public static void excludeNonTrainQuery(String queryListPath, String trainCategoryFilePath) {
		// 質問カテゴリーリストの作成
		File queryListFile = new File(queryListPath);
		if (!queryListFile.exists()) {
			System.out.println("[error] can not load file. " + queryListPath);
			return;
		}
		ArrayList<String> queryList = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(queryListFile));
			String line1 = "";
			while ((line1 = br.readLine()) != null) {
				queryList.add(line1);
			}
			// バッファのクローズ
			br.close();
		} catch (Exception e) {
			System.out.println("[error] query list path." + queryListPath + ". " + e.getMessage());
		}
		System.out.println("path1 size: " + queryListPath + ":" + queryList.size());

		// カテゴリーリストの作成
		File categoryFile = new File(trainCategoryFilePath);
		if (!categoryFile.exists()) {
			System.out.println("[error] can not load file. " + trainCategoryFilePath);
			return;
		}
		ArrayList<String> categoryList = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(categoryFile));
			String line2 = "";
			while ((line2 = br.readLine()) != null) {
				categoryList.add(line2.toUpperCase());
			}
			// バッファのクローズ
			br.close();
		} catch (Exception e) {
			System.out.println("[error] errata file." + trainCategoryFilePath + ". "
					+ e.getMessage());
		}
		System.out.println("path2 size: " + trainCategoryFilePath + ":" + categoryList.size());

		// 出力用ファイル
		String outputPath = queryListPath + "_excludeNoTrain";
		StringBuilder sb = new StringBuilder();

		// 質問リストがカテゴリーリストに入っている場合のみ出力
		for (String queryLine : queryList) {
			String[] queryParts = queryLine.split("\t");
			if (categoryList.contains(queryParts[1])) {
				sb.append(queryLine);
				sb.append("\n");
				System.out.println(queryLine);
			}
		}

		// 出力
		Output.outputFile(outputPath, sb);
	}
}
