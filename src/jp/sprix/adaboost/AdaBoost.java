package jp.sprix.adaboost;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Properties;

import jp.sprix.component.ComponentErrataData;
import jp.sprix.component.ComponentPointDoubleData;
import jp.sprix.io.Import;
import jp.sprix.io.Output;
import jp.sprix.learning.data.CaseData;
import jp.sprix.learning.data.CoordinateCaseData;
import jp.sprix.threshold.CoordinatesThreshold;
import jp.sprix.threshold.PointValueThreshold;

public class AdaBoost {
	public FinalClassifier makeClassifier(TrainCaseErrataData trainCaseErrataData,
			ComponentErrataData componentErrataData, Properties mProp) {
		StringBuilder sb = new StringBuilder();
		ArrayList<WeakClassifier> weakCassifierList = new ArrayList<WeakClassifier>();
		// 各識別器オブジェクトを生成する
		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("score", mProp)));
		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("vevmax", mProp)));
		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("vevmin", mProp)));

		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("sigmaxmax", mProp)));
		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("sigmaxmin", mProp)));
		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("sigmaymax", mProp)));
		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("sigmaymin", mProp)));

		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("perimeterMin", mProp)));
		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("perimeterMax", mProp)));
		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("innerMin0", mProp)));
		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("innerMax0", mProp)));
		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("innerMin1", mProp)));
		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("innerMax1", mProp)));
		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("angleMin0", mProp)));
		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("angleMin1", mProp)));
		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("angleMin2", mProp)));
		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("angleMin3", mProp)));
		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("angleMax0", mProp)));
		weakCassifierList.add(new WeakClassifier(new	 CoordinatesThreshold("angleMax1", mProp)));
		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("angleMax2", mProp)));
		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("angleMax3", mProp)));
		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("x0max", mProp)));
		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("x0min", mProp)));
		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("y0max", mProp)));
		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("y0min", mProp)));
		
		//weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("x2max", mProp)));
		//weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("x2min", mProp)));
		//weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("y2max", mProp)));
		//weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("y2min", mProp)));
		//weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("x3max", mProp)));
		//weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("x3min", mProp)));
		//weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("y3max", mProp)));
		//weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("y3min", mProp)));

		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("normMax0", mProp)));
		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("normMax1", mProp)));
		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("normMin0", mProp)));
		// weakCassifierList.add(new WeakClassifier(new
		// CoordinatesThreshold("normMin1", mProp)));
		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("aspectMax", mProp)));
		weakCassifierList.add(new WeakClassifier(new CoordinatesThreshold("aspectMin", mProp)));

		// 各識別器でループ
		CaseData[] caseDatas = trainCaseErrataData.getCaseDatas();
		int[] twoClasses = trainCaseErrataData.getTwoclasses();
		double[] weights = trainCaseErrataData.getWeightes();
		String minimizeThresholdStr = null;

		// 結果保存
		int boostCntMax = Integer.parseInt(mProp.getProperty("adaboost.round.cnt"));
		String[] thresholdStrArray = new String[boostCntMax];
		double[] linearCoefficients = new double[boostCntMax];

		for (int boostCnt = 0; boostCnt < boostCntMax; boostCnt++) {
			double minimizeError = 0;
			int cnt = 0;
			double error = 0;
			for (WeakClassifier weakClassifier : weakCassifierList) {
				System.out.println((boostCnt + 1) + "回目: 識別器: "
						+ weakClassifier.getThreshold().getType());
				while (!weakClassifier.isFinish()) {
					// 事例データでループ
					for (int i = 0; i < caseDatas.length; i++) {
						// 2クラス分類値の結果を算出
						CoordinateCaseData data = (CoordinateCaseData) caseDatas[i];
						int result = weakClassifier.getCoordinateTwoCassifyValue(data);
						// System.out.println(weakClassifier.getThreshold().output()
						// + "\t" + result);
						// System.out
						// .print((boostCnt + 1) + "回目: 識別器: "
						// + weakClassifier.getThreshold().getType() + " " + i +
						// ": "
						// + result);
						if (twoClasses[i] != result) {
							error += weights[i];
							// System.out.println(error);
						}
					}

					// 1つの弱識別器の中で最小のエラーを保存
					if (cnt == 0) {
						minimizeError = error;
						minimizeThresholdStr = weakClassifier.getThreshold().output();
					} else {
						if (minimizeError > error) {
							minimizeError = error;
							minimizeThresholdStr = weakClassifier.getThreshold().output();
						}
					}
					
					// debug時
					System.out.print((boostCnt + 1) + " error: " + error + "\t");
					System.out.print("threshold: " + weakClassifier.getThreshold().output() + "\t");
					System.out.print("minimize: " + minimizeError + "\t");
					System.out.println("minimizethreshold: " + minimizeThresholdStr);

					// 閾値の変化
					weakClassifier.next();
					error = 0;
					cnt++;
				}
				// 周り終わったらリセット
				weakClassifier.resetFinish();
			}

			// 現在のエラーから結合線形係数を取得
			double linerCoefficient = getLinearCoefficient(minimizeError);

			// 最小のエラーの閾値情報を保存
			thresholdStrArray[boostCnt] = minimizeThresholdStr;
			linearCoefficients[boostCnt] = linerCoefficient;

			// 出力
			System.out.println(minimizeThresholdStr + "\t" + linerCoefficient);
			sb.append(minimizeThresholdStr);
			sb.append("\t");
			sb.append(linerCoefficient);
			sb.append("\n");

			// 重みの合計
			double totalWeight = 0;

			// 重みの更新
			double[] tmpWeights = new double[weights.length];
			for (int j = 0; j < weights.length; j++) {
				CoordinateCaseData data = (CoordinateCaseData) caseDatas[j];
				double nextWeight = (double) weights[j]
						* Math.exp(((double) -1)
								* linerCoefficient
								* twoClasses[j]
								* (double) getTwoClassesByCoordinatesThreshold(
										minimizeThresholdStr, data, componentErrataData, mProp));
				tmpWeights[j] = nextWeight;
				totalWeight += nextWeight;
			}

			// 重みの正規化
			for (int k = 0; k < weights.length; k++) {
				weights[k] = tmpWeights[k] / totalWeight;
				// System.out.println(weights[k]);
			}
		}
		FinalClassifier finalClassifier = new FinalClassifier();
		finalClassifier.setLinearCoefficients(linearCoefficients);
		finalClassifier.setThresholdStrArray(thresholdStrArray);

		// 結果の出力
		Output.outputFile(mProp.getProperty("adaboost.result.output"), sb);

		return finalClassifier;
	}

	public FinalClassifier makeClassifier(TrainCasePointData trainCasePointData,
			ComponentPointDoubleData componentPointData) {
		Properties prop = Import.importProperty();
		StringBuilder sb = new StringBuilder();
		ArrayList<WeakClassifier> weakCassifierList = new ArrayList<WeakClassifier>();
		// 各識別器オブジェクトを生成する
		weakCassifierList.add(new WeakClassifier(new PointValueThreshold("xMin")));
		weakCassifierList.add(new WeakClassifier(new PointValueThreshold("xMax")));
		weakCassifierList.add(new WeakClassifier(new PointValueThreshold("yMin")));
		weakCassifierList.add(new WeakClassifier(new PointValueThreshold("yMax")));
		weakCassifierList.add(new WeakClassifier(new PointValueThreshold("distanseMax1")));
		weakCassifierList.add(new WeakClassifier(new PointValueThreshold("distanseMax2")));
		weakCassifierList.add(new WeakClassifier(new PointValueThreshold("distanseMin1")));
		weakCassifierList.add(new WeakClassifier(new PointValueThreshold("distanseMin2")));

		// 各識別器でループ
		CaseData[] caseDatas = trainCasePointData.getCaseDatas();
		int[] twoClasses = trainCasePointData.getTwoclasses();
		double[] weights = trainCasePointData.getWeightes();
		String minimizeThresholdStr = null;

		// 結果保存
		int boostCntMax = Integer.parseInt(prop.getProperty("adaboost.round.cnt"));
		String[] thresholdStrArray = new String[boostCntMax];
		double[] linearCoefficients = new double[boostCntMax];

		// 設定回数分繰り返す
		for (int boostCnt = 0; boostCnt < boostCntMax; boostCnt++) {
			double minimizeError = 0;
			int cnt = 0;
			double error = 0;
			for (WeakClassifier weakClassifier : weakCassifierList) {
				while (!weakClassifier.isFinish()) {
					// 事例データでループ
					for (int i = 0; i < caseDatas.length; i++) {
						// 2クラス分類値の結果を算出
						CoordinateCaseData data = (CoordinateCaseData) caseDatas[i];
						ArrayList<Point2D.Double> points = data.getPointList();

						// 1点の場合に対応
						// TODO 複数点
						Point2D.Double point = points.get(0);

						// TODO 複数点だったら、point[]を引数で渡せるように
						int result = weakClassifier.getPointTwoCassifyValue(point,
								componentPointData);

						if (twoClasses[i] != result) {
							error += weights[i];
						}
					}

					// 1つの弱識別器の中で最小のエラーを保存
					if (cnt == 0) {
						minimizeError = error;
						minimizeThresholdStr = weakClassifier.getThreshold().output();
					} else {
						if (minimizeError > error) {
							minimizeError = error;
							minimizeThresholdStr = weakClassifier.getThreshold().output();
						}
					}
					// System.out.print("error: " + error + "\t");
					// System.out.print("correct: " + correct + "\t");
					// System.out.print("threshold: " +
					// weakClassifier.getThreshold().output() + "\t");
					// System.out.print("minimize: " + minimizeError + "\t");
					// System.out.println("minimizethreshold: " +
					// minimizeThresholdStr);

					// 閾値の変化
					weakClassifier.next();
					error = 0;
					cnt++;
				}
				// 周り終わったらリセット
				weakClassifier.resetFinish();
			}

			// 現在のエラーから結合線形係数を取得
			double linerCoefficient = getLinearCoefficient(minimizeError);

			// 最小のエラーの閾値情報を保存
			thresholdStrArray[boostCnt] = minimizeThresholdStr;
			linearCoefficients[boostCnt] = linerCoefficient;

			// 出力
			// System.out.println(minimizeThresholdStr + "\t" +
			// linerCoefficient);
			// sb.append(minimizeThresholdStr);
			// sb.append("\t");
			// sb.append(linerCoefficient);
			// sb.append("\n");

			// 重みの合計
			double totalWeight = 0;

			// 重みの更新
			double[] tmpWeights = new double[weights.length];
			for (int j = 0; j < weights.length; j++) {
				CoordinateCaseData data = (CoordinateCaseData) caseDatas[j];
				ArrayList<Point2D.Double> points = data.getPointList();

				// TODO 複数点(i.e. pointsを渡せるように)
				Point2D.Double point = points.get(0);

				double nextWeight = (double) weights[j]
						* Math.exp(((double) -1)
								* linerCoefficient
								* twoClasses[j]
								* (double) getTwoClassesByPointValueThreshold(minimizeThresholdStr,
										point, componentPointData));
				tmpWeights[j] = nextWeight;
				totalWeight += nextWeight;
			}

			// 重みの正規化
			for (int k = 0; k < weights.length; k++) {
				weights[k] = tmpWeights[k] / totalWeight;
				// System.out.println(weights[k]);
			}
		}
		FinalClassifier finalClassifier = new FinalClassifier();
		finalClassifier.setLinearCoefficients(linearCoefficients);
		finalClassifier.setThresholdStrArray(thresholdStrArray);

		// 結果の出力
		Output.outputFile(prop.getProperty("adaboost.result.output"), sb);

		return finalClassifier;
	}

	/**
	 * 結合線形係数を取得する
	 * 
	 * @param error
	 * @return double 結合線形係数
	 */
	public double getLinearCoefficient(double error) {
		return Math.log((((double) 1 - error) / error)) / (double) 2;
	}

	/**
	 * 指定された閾値を使った識別器による結果を返す
	 * 
	 * @param minimizeThresholdStr
	 */
	public int getTwoClassesByPointValueThreshold(String minimizeThresholdStr,
			Point2D.Double point, ComponentPointDoubleData componentPointData) {
		PointValueThreshold threshold = new PointValueThreshold();
		threshold.importThreshold(minimizeThresholdStr);
		return threshold.getTwoCassifyValue(point, componentPointData);
	}

	/**
	 * 
	 * 
	 * @param minimizeThresholdStr
	 * @param data
	 * @param componentErrataData
	 * @return
	 */
	public int getTwoClassesByCoordinatesThreshold(String minimizeThresholdStr,
			CoordinateCaseData data, ComponentErrataData componentErrataData, Properties mProp) {
		CoordinatesThreshold threshold = new CoordinatesThreshold(mProp);
		threshold.importThreshold(minimizeThresholdStr);
		return threshold.getTwoCassifyValue(data);
	}

	/**
	 * 最終識別器を使用して、訓練caseDatasを分類する
	 * 
	 * @param finalClassifier
	 *            最終識別器
	 * @param trainCasePointData
	 *            訓練事例オブジェクト
	 * @param componentPointData
	 *            事例全体オブジェクト
	 * @return
	 */
	public int[] getFinalClassifyValue(FinalClassifier finalClassifier,
			TrainCasePointData trainCasePointData, ComponentPointDoubleData componentPointData) {
		// 点ごとに最終識別器を使用して分類する
		CaseData[] caseDatas = trainCasePointData.getCaseDatas();
		int[] finalClassify = new int[caseDatas.length];
		for (int i = 0; i < caseDatas.length; i++) {
			CoordinateCaseData data = (CoordinateCaseData) caseDatas[i];
			ArrayList<Point2D.Double> points = data.getPointList();
			// TODO 複数点
			Point2D.Double point = points.get(0);

			double[] linearCoefficients = finalClassifier.getLinearCoefficients();
			String[] thresholdStrArray = finalClassifier.getThresholdStrArray();
			double signFunctionArg = 0;
			for (int j = 0; j < thresholdStrArray.length; j++) {
				PointValueThreshold threshold = new PointValueThreshold();
				threshold.importThreshold(thresholdStrArray[j]);
				signFunctionArg += linearCoefficients[j]
						* ((double) threshold.getTwoCassifyValue(point, componentPointData));

			}
			if (signFunctionArg > 0) {
				finalClassify[i] = 1;
			} else if (signFunctionArg == 0) {
				finalClassify[i] = 0;
			} else if (signFunctionArg < 0) {
				finalClassify[i] = -1;
			}
		}
		return finalClassify;

	}

	/**
	 * 訓練精度測定用の識別結果を取得
	 * 
	 * @param finalClassifier
	 * @param trainCaseErrataData
	 * @param componentErrataData
	 * @param mProp
	 * @return
	 */
	public int[] getFinalClassifyValue(FinalClassifier finalClassifier,
			TrainCaseErrataData trainCaseErrataData, ComponentErrataData componentErrataData,
			Properties mProp) {
		// 点ごとに最終識別器を使用して分類する
		CaseData[] caseDatas = trainCaseErrataData.getCaseDatas();
		int[] finalClassify = new int[caseDatas.length];
		for (int i = 0; i < caseDatas.length; i++) {
			CoordinateCaseData data = (CoordinateCaseData) caseDatas[i];

			double[] linearCoefficients = finalClassifier.getLinearCoefficients();
			String[] thresholdStrArray = finalClassifier.getThresholdStrArray();
			double signFunctionArg = 0;
			for (int j = 0; j < thresholdStrArray.length; j++) {
				CoordinatesThreshold threshold = new CoordinatesThreshold(mProp);
				threshold.importThreshold(thresholdStrArray[j]);
				signFunctionArg += linearCoefficients[j]
						* ((double) threshold.getTwoCassifyValue(data));
			}
			if (signFunctionArg > 0) {
				finalClassify[i] = 1;
			} else if (signFunctionArg == 0) {
				finalClassify[i] = 0;
			} else if (signFunctionArg < 0) {
				finalClassify[i] = -1;
			}
		}
		return finalClassify;
	}

	/**
	 * 汎化精度測定用の識別結果を取得
	 * 
	 * @param finalClassifier
	 * @param trainCaseErrataData
	 * @param componentErrataData
	 * @param mProp
	 * @return
	 */
	public int[] getFinalClassifyValue(FinalClassifier finalClassifier,
			QueryCaseErrataData queryCaseErrataData, ComponentErrataData componentErrataData,
			Properties mProp) {
		// 点ごとに最終識別器を使用して分類する
		CaseData[] caseDatas = queryCaseErrataData.getCaseDatas();
		int[] finalClassify = new int[caseDatas.length];
		for (int i = 0; i < caseDatas.length; i++) {
			CoordinateCaseData data = (CoordinateCaseData) caseDatas[i];

			double[] linearCoefficients = finalClassifier.getLinearCoefficients();
			String[] thresholdStrArray = finalClassifier.getThresholdStrArray();
			double signFunctionArg = 0;
			for (int j = 0; j < thresholdStrArray.length; j++) {
				CoordinatesThreshold threshold = new CoordinatesThreshold(mProp);
				threshold.importThreshold(thresholdStrArray[j]);
				signFunctionArg += linearCoefficients[j]
						* ((double) threshold.getTwoCassifyValue(data));

			}
			if (signFunctionArg > 0) {
				finalClassify[i] = 1;
			} else if (signFunctionArg == 0) {
				finalClassify[i] = 0;
			} else if (signFunctionArg < 0) {
				finalClassify[i] = -1;
			}
		}
		return finalClassify;
	}
}
