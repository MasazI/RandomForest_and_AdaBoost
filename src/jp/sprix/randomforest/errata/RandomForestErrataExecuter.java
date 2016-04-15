package jp.sprix.randomforest.errata;

import java.util.ArrayList;
import java.util.Properties;

import jp.sprix.component.ComponentErrataData;
import jp.sprix.component.ComponentPointIntData;
import jp.sprix.gui.ProtWindow;
import jp.sprix.io.Import;
import jp.sprix.sample.Sampling;

/**
 * ランダムフォレスト実行クラス
 * 
 * @author root
 * 
 */
public class RandomForestErrataExecuter {
	/**
	 * ランダムフォレスト実行
	 * 
	 * @param String
	 *            errataFilePath 事例Errataデータ
	 */
	public static void randomForestTrainErrata(String errataFilePath, String anotherErrataFilePath) {
		Properties prop = Import.importProperty();

		// 事例の取得
		ArrayList<String[]> errataLines = Import.getArrayListFromFile(errataFilePath);
		ComponentErrataData componentErrataData = new ComponentErrataData(errataLines);

		// 正事例と負事例の判定を行いながら、事例を作成
		RandomForestErrataCase errataCase = new RandomForestErrataCase(componentErrataData);

		// 木の数だけサンプリングを行う
		int treeCnt = Integer.parseInt(prop.getProperty("randomforest.errata.tree.cnt"));
		int samplingCnt = Integer.parseInt(prop.getProperty("randomforest.errata.sampling.cnt"));
		int samplingSeed = Integer.parseInt(prop.getProperty("randomforest.errata.sampling.seed"));
		// サンプリング集合の作成
		RandomForestSampling randomForestSampling = new RandomForestSampling(treeCnt);
		for (int i = 0; i < treeCnt; i++) {
			randomForestSampling.addSampling(i, Sampling.sampling(samplingCnt,
					errataCase.getRandomforest_sampling_max(), samplingSeed + i));
		}

		// train
		RandomForestSample rondomForest = new RandomForestSample(treeCnt);
		rondomForest.train(errataCase, randomForestSampling);

		// 全事例を使用した精度測定
		rondomForest.validation(errataCase);

		// 別の事例を使用した汎化精度測定
		ArrayList<String[]> anotherErrataLines = Import.getArrayListFromFile(anotherErrataFilePath);
		ComponentErrataData componentAnotherErrataData = new ComponentErrataData(anotherErrataLines);
		RandomForestErrataCase anotherErrataCase = new RandomForestErrataCase(
				componentAnotherErrataData);
		rondomForest.validation(anotherErrataCase);

		// 精度出力
		String filePath = prop.getProperty("randomforest.classify.output");
		errataCase.outputClassify(filePath, prop);

		// 汎化精度出力
		String generalFilePath = prop.getProperty("randomforest.general.classify.output");
		anotherErrataCase.outputClassify(generalFilePath, prop);

		// Display (query)

		System.out.println("[randomforest] debug point");
	}
}
