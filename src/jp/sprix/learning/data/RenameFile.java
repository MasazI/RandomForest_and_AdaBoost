package jp.sprix.learning.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class RenameFile {
	public static void renameFile(String categoryPath, String renameHOmeDir) {
		// ファイル名変更するカテゴリーリスト
		File file = new File(categoryPath);
		if (!file.exists()) {
			System.out.println("[error] can not load errata file. " + categoryPath);
			return;
		}
		ArrayList<String> fileLines = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				fileLines.add(line);
			}
			// バッファのクローズ
			br.close();
		} catch (Exception e) {
			System.out.println("[error] errata file." + categoryPath + ". " + e.getMessage());
		}

		// カテゴリーの存在するディレクトリ
		for (String category : fileLines) {
			String path = renameHOmeDir + "/" + category;

			File renameDir = new File(path);
			File[] files = renameDir.listFiles();
			for (File renameFile : files) {
				String[] fileParts = renameFile.getName().split("-");
				String[] renameFileParts = fileParts[0].split("_");
				String afterPath = null;
				for (int i = 0; i < renameFileParts.length; i++) {
					if (i != 0) {
						afterPath += "_";
						if (i == renameFileParts.length - 1) {
							afterPath += "0";
						}
						afterPath += renameFileParts[i];
					} else {
						afterPath = renameFileParts[i];
					}
				}

				String finalPath = null;
				for (int j = 0; j < fileParts.length; j++) {
					if (j == 0) {
						finalPath = afterPath;
					} else {
						finalPath += fileParts[j];
					}
				}

				renameFile.renameTo(new File(renameFile.getParent(), finalPath));
			}

		}

	}
}
