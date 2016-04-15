package jp.sprix.learning;

import java.io.File;
import java.util.ArrayList;

import jp.sprix.adaboost.AdaBoostExecuter;
import jp.sprix.cv.TrainImage;
import jp.sprix.gui.ProtWindow;
import jp.sprix.learning.data.ClassifyImageFile;
import jp.sprix.learning.data.FileLineDifference;
import jp.sprix.learning.data.ImageDataArrange;
import jp.sprix.learning.data.RenameFile;
import jp.sprix.learning.evaluation.EvaluationRunning;
import jp.sprix.learning.filter.ComponentExecuter;
import jp.sprix.learning.filter.ErrataFilter;
import jp.sprix.learning.post.PostImageDataRunnning;
import jp.sprix.learning.report.ReportRunning;
import jp.sprix.randomforest.RandomForestExecuter;
import jp.sprix.randomforest.errata.RandomForestErrataExecuter;

/**
 * sprix recognize engine & filter Decision class
 * 
 * [Verify] post
 * /var/local/imagelist配下のimage_data_[質問のタイプ].tsvにリストされた質問画像をエンジンへポストし、
 * repost作成用のerrataファイルを/var/local/errata/[質問のタイプ]に出力する。
 * 
 * [Verify]report 指定したerrataファイルを元に、recall、precision、erra率を出力する。
 * 数値に反映するカテゴリーは訓練済みのものとし、/media/sf_images/settings/tall/category_names.tsv
 * に記載の内カテゴリーの質問画像はextractする。
 * 
 * @author root
 * 
 */
public class Decision {
	private static final String USAGE_ALL = "[Usage]first argument: classify, filelist, arrange, import, report";
	private static final String USAGE_CLASSIFY = "[Usage]Decision classify <クラスリストファイルのパス> <画像ファイルのディレクトリ> <train or query>";
	private static final String USAGE_QUERY_EXCLUDE = "[Usage]Decision query exclude <質問リストのパス> <訓練カテゴリーリストパス>";
	private static final String USAGE_FILELIST = "[Usage]Decision filelist <出力したいファイルを格納したディレクトリ>";
	private static final String USAGE_TEXT = "[Usage] Decision textname <教科書名を出力したいカテゴリー名のリスト> <カテゴリーと教科書名の対応リストtsv>";
	private static final String USAGE_POST = "[Usage] Decision post <何位まで取得するか>";
	private static final String USAGE_EVALUATION = "[Usage] Decision errata <何位まで解析するか>";
	private static final String USAGE_REPORT = "[Usage] Decision report <errataファイルパス> <質問のタイプ（all(訓練と等しい画像)、part(訓練画像の部分の場合)、train(訓練画像自身をポストする)）> <何まで解析するか>";
	private static final String USAGE_BATCH = "[Usage] Decision batch <errataファイルパス> <質問のタイプ（all(訓練と等しい画像)、part(訓練画像の部分の場合)、train(訓練画像自身をポストする)）>";
	private static final String USAGE_FILTER = "[Usage] Decision filter <errataファイルパス> <角度> <周囲最大値> <周囲最小値>";
	private static final String USAGE_COMPONENT = "[Usage] Decision component <errataファイルパス>";
	private static final String USAGE_ADABOOST = "[Usage] adaboost <sample or errata>";
	private static final String USAGE_ADABOOST_ERRATA = "[Usage] adaboost errata <errataファイルパス>";
	private static final String USAGE_ADABOOST_QUERY = "[Usage] adaboost query <errataファイルパス> <辞書ファイルパス>";
	private static final String USAGE_RANDOM_FOREST = "[Usage] randomforest <sample or errata> <if you select errata, errataファイルパス>";
	private static final String USAGE_RANDOM_FOREST_ERRATA = "[Usage] randomforest errata <errataファイルパス> <汎化精度用errataファイルパス>";

	// 廃止
	// private static final String usage =
	// "[Usage]java Decision <import or report> <filepath> <train or all or part>";

	/**
	 * 辞書作成・補助ツール
	 * 
	 * @param args
	 *            コマンドラインから取得する引数
	 */
	public static void main(String args[]) {
		if (args.length < 1) {
			System.out.println(USAGE_ALL);
			System.exit(0);
		}

		if (args[0].equals("rename")) {
			RenameFile.renameFile(args[1], args[2]);
			System.exit(0);
		}

		// 不正な訓練画像を抽出する
		if (args[0].equals("train")) {
			if (args[1].equals("extract")) {
				TrainImage.extractionNostandardized(args[2]);
			}
			System.exit(0);
		}

		if (args[0].equals("difference")) {
			FileLineDifference.difference(args[1], args[2]);
			System.exit(0);
		}

		// duplicate
		if (args[0].equals("train")) {
			if (args[1].equals("duplicate")) {
				TrainImage.dupulicateImage(args[2]);
			}
		}

		// 画像をクラス(カテゴリー)のディレクトリへ移動する
		if (args[0].equals("classify")) {
			if (args.length < 4) {
				System.out.println(USAGE_CLASSIFY);
				System.exit(0);
			}
			System.out.println("[classify image list] start");
			ClassifyImageFile.classifyImage(args[1], args[2], args[3]);
			System.out.println("[classify image list] finish");
			System.exit(0);
		}

		// 指定したディレクトリにあるイメージファイル名のリストを出力する
		if (args[0].equals("filelist")) {
			if (args.length < 2) {
				System.out.println(USAGE_FILELIST);
				System.exit(0);
			}
			System.out.println("[image file list] start");
			ImageDataArrange trainDataArrange = new ImageDataArrange();
			trainDataArrange.outputTrainImageFilePath(args[1]);
			trainDataArrange.closeTrainImagePathWriter();
			System.out.println("[image file list] finish");
			System.exit(0);
		}

		// 質問画像の整理
		if (args[0].equals("query")) {
			if (args.length < 3) {
				System.out.println(USAGE_QUERY_EXCLUDE);
				System.exit(0);
			}
			// 訓練画像の無い質問を除外したexampleを作成する
			if (args[1].equals("exclude")) {
				ImageDataArrange.excludeNonTrainQuery(args[2], args[3]);
			}
			System.exit(0);
		}

		// イメージファイルのカテゴリーが対応する教科書名を出力する
		if (args[0].equals("textname")) {
			if (args.length < 3) {
				System.out.println(USAGE_TEXT);
				System.exit(0);
			}
			System.out.println("[arrange data] start");
			ArrayList<String> categoryNames = ImageDataArrange.getCategoryNameList(args[1]);
			ArrayList<String> textNames = ImageDataArrange.getTextNameList(categoryNames, args[2]);
			ImageDataArrange.outputTextNameList(textNames);
			System.out.println("[arrange data] finish");
			System.exit(0);
		}

		// イメージデータをポストして結果を出力する
		if (args[0].equals("post")) {
			System.out.println("[post] start");
			// 引数の確認
			if (args.length < 2) {
				System.out.print(USAGE_POST);
				System.exit(0);
			}

			// all固定
			PostImageDataRunnning corrdinateRunning = new PostImageDataRunnning();
			corrdinateRunning.postImageDataStart("all", Integer.parseInt(args[1]));
			System.out.println("[post] finish");
			System.exit(0);
		}

		// 既存のresponseを使用して、新しいerrataファイルの作成
		if (args[0].equals("errata")) {
			if (args.length < 2) {
				System.out.println(USAGE_EVALUATION);
				System.exit(0);
			}
			System.out.println("[errata] start");
			EvaluationRunning evaluationRunning = new EvaluationRunning();
			evaluationRunning.evaluation("all", Integer.parseInt(args[1]));
			System.out.println("[errata] finish");
			System.exit(0);
		}

		// 結果レポートの出力
		if (args[0].equals("report")) {
			if (args.length < 3) {
				System.out.print(USAGE_REPORT);
				System.exit(0);
			}

			File file = new File(args[1]);
			if (file.isDirectory()) {
				System.out.print(USAGE_REPORT);
				System.exit(0);
			}
			System.out.println("[report] start");
			ReportRunning reportRunning = new ReportRunning();
			reportRunning.outputReport(args[1], args[2]);
			System.out.println("[report] finish");
			System.exit(0);
		}

		if (args[0].equals("filter")) {
			if (args.length < 5) {
				System.out.println(USAGE_FILTER);
				System.exit(0);
			}

			System.out.println("[filter] start");
			ErrataFilter.filterCoordinate(args[1], args[2], args[3], args[4]);
			System.out.println("[filter] finish");
			System.exit(0);
		}

		if (args[0].equals("batch")) {
			if (args[0].length() < 3) {
				System.out.println(USAGE_BATCH);
			}

			System.out.println("[batch] start");
			ErrataFilter.filterCoordinateLearning(args[1], args[2]);
			System.out.println("[batch] finish");
			System.exit(0);
		}

		if (args[0].equals("component")) {
			if (args[0].length() < 2) {
				System.out.println(USAGE_COMPONENT);
			}

			System.out.println("[component] start");
			ComponentExecuter.componentFromErrataExec(args[1]);
			System.out.println("[component] finish");
			System.exit(0);
		}

		if (args[0].equals("adaboost")) {
			if (args.length < 2) {
				System.out.println(USAGE_ADABOOST);
				System.exit(0);
			}
			System.out.println("[adaboost] start");
			if (args[1].equals("sample")) {
				System.out.println("[adaboost-type] sample");
				AdaBoostExecuter.adaBoostSample();

			} else if (args[1].equals("errata")) {
				if (args.length < 3) {
					System.out.println(USAGE_ADABOOST_ERRATA);
					System.exit(0);
				}
				System.out.println("[adaboost-type] errata");
				AdaBoostExecuter.adaBoostErrata(args[2]);
			} else if (args[1].equals("query")) {
				if (args.length < 3) {
					System.out.println(USAGE_ADABOOST_QUERY);
					System.exit(0);
				}
				AdaBoostExecuter.adaBoostErrataQuery(args[2], args[3]);
			}
			System.out.println("[adaboost] finish");
		}

		if (args[0].equals("randomforest")) {
			if (args.length < 2) {
				System.out.println(USAGE_RANDOM_FOREST);
				System.exit(0);
			}
			if (args[1].equals("sample")) {
				RandomForestExecuter.randomForestTrainSample();
			} else if (args[1].equals("errata")) {
				if (args.length < 3) {
					System.out.println(USAGE_RANDOM_FOREST_ERRATA);
					System.exit(0);
				}
				RandomForestErrataExecuter.randomForestTrainErrata(args[2], args[3]);
			}
		}

		// debug
		// if (args[0].equals("display")) {
		// ProtWindow pw = new ProtWindow();
		// pw.displaySample();
		// }
	}
}
