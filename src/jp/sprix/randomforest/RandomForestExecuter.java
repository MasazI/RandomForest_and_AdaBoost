package jp.sprix.randomforest;

import java.util.Properties;

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
public class RandomForestExecuter {
	/**
	 * ランダムフォレスト実行　（サンプル）
	 */
	public static void randomForestTrainSample() {
		Properties prop = Import.importProperty();

		// 事例の作成
		int sampleAllCnt = Integer.parseInt(prop.getProperty("randomforest.sample.allcnt"));
		int sampleMaxNumber = Integer.parseInt(prop.getProperty("randomforest.sample.max"));
		// 正事例と負事例の判定が可能な事例コンポーネントクラス
		ComponentPointIntData componentPointData = new ComponentPointIntData("randomforest");
		// 正事例と負事例の判定を行いながら、事例を作成
		RandomForestSampleCase sampleCase = new RandomForestSampleCase(sampleAllCnt,
				sampleMaxNumber, componentPointData);

		// Display (train)
		ProtWindow window = new ProtWindow();
		window.displaySample(componentPointData);

		// 木の数だけサンプリングを行う
		int treeCnt = Integer.parseInt(prop.getProperty("randomforest.tree.cnt"));
		int samplingCnt = Integer.parseInt(prop.getProperty("randomforest.sampling.cnt"));
		int samplingMax = Integer.parseInt(prop.getProperty("randomforest.sampling.max"));
		int samplingSeed = Integer.parseInt(prop.getProperty("randomforest.sampling.seed"));
		// サンプリング集合の作成
		RandomForestSampling randomForestSampling = new RandomForestSampling(treeCnt);
		for (int i = 0; i < treeCnt; i++) {
			randomForestSampling.addSampling(i,
					Sampling.sampling(samplingCnt, samplingMax, samplingSeed + i));
		}

		// train
		RandomForestSample rondomForest = new RandomForestSample(treeCnt);
		rondomForest.train(sampleCase, randomForestSampling);

		// 訓練精度
		rondomForest.validation(sampleCase);
		componentPointData.rebuild(sampleCase);
		// Display (query)
		ProtWindow trainResultWindow = new ProtWindow();
		trainResultWindow.displayQuery(componentPointData, "train result");

		// validation
		// 正事例と負事例の判定を行いながら、事例を作成
		RandomForestSampleCase queryCase = new RandomForestSampleCase(sampleAllCnt,
				sampleMaxNumber, componentPointData);
		rondomForest.validation(queryCase);
		componentPointData.rebuild(queryCase);

		// Display (query)
		ProtWindow resultWindow = new ProtWindow();
		resultWindow.displayQuery(componentPointData, "query result");

		System.out.println("[randomforest] debug point");
	}
}
