package jp.sprix.threshold;

import java.awt.geom.Point2D;
import java.util.Properties;

import jp.sprix.cv.CVPointGeometry;
import jp.sprix.io.Import;
import jp.sprix.learning.data.CoordinateCaseData;

/**
 * 閾値クラス
 * 
 * 与えられたパラメータを少しずつ変化させるクラス
 * 
 * @author root
 * 
 */
public class CoordinatesThreshold implements Threshold {
	// 閾値タイプ（angleMin[0-3]、angleMax[0-3]、perimeterMin、perimeterMax、innerMax[0-1]、innerMin[0-1]、score）
	private String type = null;

	// 角度
	private double angleThreshold = 0;

	private double angleThresholdEnd = 0;

	private double angleStep = 0;

	// 周囲の最小値
	private double perimeterThreshold = 0;

	private double perimeterThresholdEnd = 0;

	private double perimeterStep = 0;

	// 内積
	private double innerThreshold = 0;

	private double innerThresholdEnd = 0;

	private double innerStep = 0;

	// Scoreの閾値
	private double scoreThreshold = 0;

	private double scoreThresholdEnd = 0;

	private double scoreStep = 0;

	// 向かい合う辺の比
	private double normThreshold = 0;

	private double normThresholdEnd = 0;

	private double normStep = 0;

	// アスペクト比
	private double aspectThreshold = 0;

	private double aspectThresholdEnd = 0;

	private double aspectStep = 0;

	// finish フラグ
	private boolean isFinish = false;

	// 左上座標x
	private double linerThreshold = 0;

	private double linerThresholdEnd = 0;

	// 座標step
	private double linerStep = 0;

	// 共分散行列の固有値
	private double mVarianceEigenValueThreshold = 0;

	private double mVarianceEigenValueThresholdEnd = 0;

	private double mVarianceEigenValueThresholdStep = 0;

	// 座標情報の分散
	private double mSigmaValueThreshold = 0;

	private double mSigmaValueThresholdEnd = 0;

	private double mSigmaValueThresholdStep = 0;

	/**
	 * 引数なしコンストラクタ
	 */
	public CoordinatesThreshold(Properties mProp) {
		// setValue();
		setStep(mProp);
	}

	/**
	 * type指定コンストラクタ
	 * 
	 * @param type
	 */
	public CoordinatesThreshold(String type, Properties mProp) {
		this.type = type;
		setValue();
		setStep(mProp);
	}

	private void setStep(Properties mProp) {
		angleStep = Double.parseDouble(mProp.getProperty("adaboost.anglemax.step"));
		perimeterStep = Double.parseDouble(mProp.getProperty("adaboost.perimeter.step"));
		innerStep = Double.parseDouble(mProp.getProperty("adaboost.inner.step"));
		scoreStep = Double.parseDouble(mProp.getProperty("adaboost.score.step"));
		normStep = Double.parseDouble(mProp.getProperty("adaboost.norm.step"));
		aspectStep = Double.parseDouble(mProp.getProperty("adaboost.aspect.step"));
		linerStep = Double.parseDouble(mProp.getProperty("adaboost.liner.step"));
		mVarianceEigenValueThresholdStep = Double.parseDouble(mProp
				.getProperty("adaboost.varianceEigenValue.step"));
		mSigmaValueThresholdStep = Double
				.parseDouble(mProp.getProperty("adaboost.sigmaValue.step"));

	}

	/**
	 * 閾値の値をセットする
	 */
	private void setValue() {
		if (type == null) {
			System.out.println("[error] type is null.");
			return;
		}
		Properties prop = Import.importProperty();
		if (type.equals("angleMin0") || type.equals("angleMin1") || type.equals("angleMin2")
				|| type.equals("angleMin3")) {
			angleThreshold = Double.parseDouble(prop.getProperty("adaboost.anglemin.start"));
			angleThresholdEnd = Double.parseDouble(prop.getProperty("adaboost.anglemin.end"));
		} else if (type.equals("angleMax0") || type.equals("angleMax1") || type.equals("angleMax2")
				|| type.equals("angleMax3")) {
			angleThreshold = Double.parseDouble(prop.getProperty("adaboost.anglemax.start"));
			angleThresholdEnd = Double.parseDouble(prop.getProperty("adaboost.anglemax.end"));
		} else if (type.equals("perimeterMin")) {
			perimeterThreshold = Double
					.parseDouble(prop.getProperty("adaboost.perimetermin.start"));
			perimeterThresholdEnd = Double.parseDouble(prop
					.getProperty("adaboost.perimetermin.end"));
		} else if (type.equals("perimeterMax")) {
			perimeterThreshold = Double
					.parseDouble(prop.getProperty("adaboost.perimetermax.start"));
			perimeterThresholdEnd = Double.parseDouble(prop
					.getProperty("adaboost.perimetermax.end"));
		} else if (type.equals("innerMax0") || type.equals("innerMin0") || type.equals("innerMax1")
				|| type.equals("innerMin1")) {
			innerThreshold = Double.parseDouble(prop.getProperty("adaboost.inner.start"));
			innerThresholdEnd = Double.parseDouble(prop.getProperty("adaboost.inner.end"));
		} else if (type.equals("score")) {
			scoreThreshold = Double.parseDouble(prop.getProperty("adaboost.score.start"));
			scoreThresholdEnd = Double.parseDouble(prop.getProperty("adaboost.score.end"));
		} else if (type.equals("norm0") || type.equals("norm1")) {
			normThreshold = Double.parseDouble(prop.getProperty("adaboost.norm.start"));
			normThresholdEnd = Double.parseDouble(prop.getProperty("adaboost.norm.end"));
		} else if (type.equals("aspect")) {
			aspectThreshold = Double.parseDouble(prop.getProperty("adaboost.aspect.start"));
			aspectThresholdEnd = Double.parseDouble(prop.getProperty("adaboost.aspect.end"));
		} else if (type.equals("x0max") || type.equals("x0min")) {
			linerThreshold = Double.parseDouble(prop.getProperty("adaboost.x0.start"));
			linerThresholdEnd = Double.parseDouble(prop.getProperty("adaboost.x0.end"));
		} else if (type.equals("y0max") || type.equals("y0min")) {
			linerThreshold = Double.parseDouble(prop.getProperty("adaboost.y0.start"));
			linerThresholdEnd = Double.parseDouble(prop.getProperty("adaboost.y0.end"));
		} else if (type.equals("x2max") || type.equals("x2min")) {
			linerThreshold = Double.parseDouble(prop.getProperty("adaboost.x2.start"));
			linerThresholdEnd = Double.parseDouble(prop.getProperty("adaboost.x2.end"));
		} else if (type.equals("y2max") || type.equals("y2min")) {
			linerThreshold = Double.parseDouble(prop.getProperty("adaboost.y2.start"));
			linerThresholdEnd = Double.parseDouble(prop.getProperty("adaboost.y2.end"));
		} else if (type.equals("x3max") || type.equals("x3min")) {
			linerThreshold = Double.parseDouble(prop.getProperty("adaboost.x3.start"));
			linerThresholdEnd = Double.parseDouble(prop.getProperty("adaboost.x3.end"));
		} else if (type.equals("y3max") || type.equals("y3min")) {
			linerThreshold = Double.parseDouble(prop.getProperty("adaboost.y3.start"));
			linerThresholdEnd = Double.parseDouble(prop.getProperty("adaboost.y3.end"));
		} else if (type.equals("y3max") || type.equals("y3min")) {
			linerThreshold = Double.parseDouble(prop.getProperty("adaboost.y3.start"));
			linerThresholdEnd = Double.parseDouble(prop.getProperty("adaboost.y3.end"));
		} else if (type.equals("vevmax") || type.equals("vevmin")) {
			mVarianceEigenValueThreshold = Double.parseDouble(prop
					.getProperty("adaboost.varianceEigenValue.start"));
			mVarianceEigenValueThresholdEnd = Double.parseDouble(prop
					.getProperty("adaboost.varianceEigenValue.end"));
		} else if (type.equals("sigmaxmax") || type.equals("sigmaxmin")) {
			mSigmaValueThreshold = Double
					.parseDouble(prop.getProperty("adaboost.sigmaValue.start"));
			mSigmaValueThresholdEnd = Double.parseDouble(prop
					.getProperty("adaboost.sigmaValue.end"));
		} else if (type.equals("sigmaymax") || type.equals("sigmaymin")) {
			mSigmaValueThreshold = Double
					.parseDouble(prop.getProperty("adaboost.sigmaValue.start"));
			mSigmaValueThresholdEnd = Double.parseDouble(prop
					.getProperty("adaboost.sigmaValue.end"));
		}
	}

	/**
	 * 閾値を次へ進める
	 */
	public void next() {
		if (type.equals("angleMin0") || type.equals("angleMax0") || type.equals("angleMin1")
				|| type.equals("angleMax1") || type.equals("angleMin2") || type.equals("angleMax2")
				|| type.equals("angleMin3") || type.equals("angleMax3")) {
			nextAngle();
		} else if (type.equals("perimeterMin") || type.equals("perimeterMax")) {
			nextPerimeter();
		} else if (type.equals("innerMax0") || type.equals("innerMin0") || type.equals("innerMax1")
				|| type.equals("innerMin1")) {
			nextInner();
		} else if (type.equals("score")) {
			nextScore();
		} else if (type.equals("normMax0") || type.equals("normMin0") || type.equals("normMax1")
				|| type.equals("normMin1")) {
			nextNorm();
		} else if (type.equals("aspectMax") || type.equals("aspectMin")) {
			nextAspect();
		} else if (type.equals("x0max") || type.equals("x0min")) {
			nextLiner();
		} else if (type.equals("y0max") || type.equals("y0min")) {
			nextLiner();
		} else if (type.equals("x2max") || type.equals("x2min")) {
			nextLiner();
		} else if (type.equals("y2max") || type.equals("y2min")) {
			nextLiner();
		} else if (type.equals("x3max") || type.equals("x3min")) {
			nextLiner();
		} else if (type.equals("y3max") || type.equals("y3min")) {
			nextLiner();
		} else if (type.equals("vevmax") || type.equals("vevmin")) {
			nextVev();
		} else if (type.equals("sigmaxmax") || type.equals("sigmaxmin") || type.equals("sigmaymax")
				|| type.equals("sigmaymin")) {
			nextSigma();
		}
	}

	/**
	 * 内角の閾値を次へ進める
	 */
	public void nextAngle() {
		angleThreshold += angleStep;
		setFinish();
	}

	/**
	 * 周囲長の閾値を次へ進める
	 */
	public void nextPerimeter() {
		perimeterThreshold += perimeterStep;
		setFinish();
	}

	/**
	 * 内積の閾値を次へ進める
	 */
	public void nextInner() {
		innerThreshold += innerStep;
		setFinish();
	}

	public void nextScore() {
		scoreThreshold += scoreStep;
		setFinish();
	}

	public void nextNorm() {
		normThreshold += normStep;
		setFinish();
	}

	public void nextAspect() {
		aspectThreshold += aspectStep;
		setFinish();
	}

	public void nextLiner() {
		linerThreshold += linerStep;
		setFinish();
	}

	public void nextVev() {
		mVarianceEigenValueThreshold += mVarianceEigenValueThresholdStep;
		setFinish();
	}

	public void nextSigma() {
		mSigmaValueThreshold += mSigmaValueThresholdStep;
		setFinish();
	}

	/**
	 * 事例クラスオブジェクトの分類
	 * 
	 * @param cordinateCaseData
	 * @return
	 */
	public int getTwoCassifyValue(CoordinateCaseData cordinateCaseData) {
		Point2D.Double[] points = cordinateCaseData.getPointList().toArray(new Point2D.Double[0]);

		// NaNが含まれていたら0(負事例として)で返す
		for (int i = 0; i < points.length; i++) {
			Point2D.Double point = points[i];
			if (Double.isNaN(point.getX()) || Double.isNaN(point.getY())) {
				return -1;
			}
		}

		// socoreでふるいにかける
		// if (cordinateCaseData.getScore() > 10) {
		// return 1;
		// }

		if (type.equals("angleMin0") || type.equals("angleMax0")) {
			return isAdaptive(CVPointGeometry.getInteriorAngle(points, 0));
		} else if (type.equals("angleMin1") || type.equals("angleMax1")) {
			return isAdaptive(CVPointGeometry.getInteriorAngle(points, 1));
		} else if (type.equals("angleMin2") || type.equals("angleMax2")) {
			return isAdaptive(CVPointGeometry.getInteriorAngle(points, 2));
		} else if (type.equals("angleMin3") || type.equals("angleMax3")) {
			return isAdaptive(CVPointGeometry.getInteriorAngle(points, 3));
		} else if (type.equals("perimeterMin") || type.equals("perimeterMax")) {
			return isAdaptive(CVPointGeometry.getPerimeter(points));
		} else if (type.equals("innerMin0") || type.equals("innerMax0")) {
			return isAdaptive(CVPointGeometry.getInner(points, 0));
		} else if (type.equals("innerMin1") || type.equals("innerMax1")) {
			return isAdaptive(CVPointGeometry.getInner(points, 1));
		} else if (type.equals("score")) {
			return isAdaptive(cordinateCaseData.getScore());
		} else if (type.equals("normMax0") || type.equals("normMin0")) {
			return isAdaptive(CVPointGeometry.getNormRatioSideBySide(points, 0));
		} else if (type.equals("normMax1") || type.equals("normMin1")) {
			return isAdaptive(CVPointGeometry.getNormRatioSideBySide(points, 1));
		} else if (type.equals("aspectMax") || type.equals("aspectMin")) {
			return isAdaptive(CVPointGeometry.getAspectRatio(points));

		} else if (type.equals("x0max") || type.equals("x0min")) {
			return isAdaptive(points[0].getX());
		} else if (type.equals("y0max") || type.equals("y0min")) {
			return isAdaptive(points[0].getY());
		} else if (type.equals("x2max") || type.equals("x2min")) {
			return isAdaptive(points[2].getX());
		} else if (type.equals("y2max") || type.equals("y2min")) {
			return isAdaptive(points[2].getY());
		} else if (type.equals("x3max") || type.equals("x3min")) {
			return isAdaptive(points[3].getX());
		} else if (type.equals("y3max") || type.equals("y3min")) {
			return isAdaptive(points[3].getY());
		} else if (type.equals("vevmax") || type.equals("vevmin")) {
			return isAdaptive(CVPointGeometry.getCoordinateVarianceCovarianceMatrix(points));
		} else if (type.equals("sigmaxmax") || type.equals("sigmaxmin")) {
			return isAdaptive(CVPointGeometry.getCoordinateSigma(points)[0]);
		} else if (type.equals("sigmaymax") || type.equals("sigmaymin")) {
			return isAdaptive(CVPointGeometry.getCoordinateSigma(points)[1]);
		}
		return 0;
	}

	/**
	 * 閾値を満たす値であれば1、そうでなければ-1を返す
	 * 
	 * @param sample
	 * @return
	 */
	private int isAdaptive(double sample) {
		// System.out.println(" " + sample + " ");

		if (type.equals("angleMin0")) {
			if (angleThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("angleMin1")) {
			if (angleThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("angleMin2")) {
			if (angleThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("angleMin3")) {
			if (angleThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("angleMax0")) {
			if (angleThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("angleMax1")) {
			if (angleThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("angleMax2")) {
			if (angleThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("angleMax3")) {
			if (angleThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("perimeterMin")) {
			if (perimeterThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("perimeterMax")) {
			if (perimeterThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("innerMin0")) {
			if (innerThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("innerMax0")) {
			if (innerThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("innerMin1")) {
			if (innerThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("innerMax1")) {
			if (innerThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("score")) {
			if (scoreThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("normMax0")) {
			if (normThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("normMin0")) {
			if (normThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("normMax1")) {
			if (normThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("normMin1")) {
			if (normThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("aspectMax")) {
			if (aspectThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("aspectMin")) {
			if (aspectThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("x0max")) {
			if (linerThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("x0min")) {
			if (linerThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("y0max")) {
			if (linerThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("y0min")) {
			if (linerThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("x2max")) {
			if (linerThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("x2min")) {
			if (linerThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("y2max")) {
			if (linerThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("y2min")) {
			if (linerThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("x3max")) {
			if (linerThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("x3min")) {
			if (linerThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("y3max")) {
			if (linerThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("y3min")) {
			if (linerThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("vevmax")) {
			if (mVarianceEigenValueThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("vevmin")) {
			if (mVarianceEigenValueThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("sigmaxmax")) {
			if (mSigmaValueThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("sigmaxmin")) {
			if (mSigmaValueThreshold >= sample) {
				return 1;
			}
		} else if (type.equals("sigmaymax")) {
			if (mSigmaValueThreshold <= sample) {
				return 1;
			}
		} else if (type.equals("sigmaymin")) {
			if (mSigmaValueThreshold >= sample) {
				return 1;
			}
		}
		return -1;
	}

	/**
	 * finishの場合にフラグを立てる
	 */
	private void setFinish() {
		if (type.equals("angleMin0") || type.equals("angleMax0") || type.equals("angleMin1")
				|| type.equals("angleMax1") || type.equals("angleMin2") || type.equals("angleMax2")
				|| type.equals("angleMin3") || type.equals("angleMax3")) {
			// 内角の閾値の場合
			if (angleThreshold >= angleThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("perimeterMin") || type.equals("perimeterMax")) {
			// 周囲の閾値の場合
			if (perimeterThreshold > perimeterThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("innerMax0") || type.equals("innerMin0") || type.equals("innerMax1")
				|| type.equals("innerMin1")) {
			// 内積
			if (innerThreshold > innerThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("score")) {
			// スコア
			if (scoreThreshold > scoreThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("normMax0") || type.equals("normMin0") || type.equals("normMax1")
				|| type.equals("normMin1")) {
			if (normThreshold > normThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("aspectMax") || type.equals("aspectMin")) {
			if (aspectThreshold > aspectThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("x0max") || type.equals("x0min")) {
			if (linerThreshold > linerThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("y0max") || type.equals("y0min")) {
			if (linerThreshold > linerThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("x2max") || type.equals("x2min")) {
			if (linerThreshold > linerThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("y2max") || type.equals("y2min")) {
			if (linerThreshold > linerThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("x3max") || type.equals("x3min")) {
			if (linerThreshold > linerThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("y3max") || type.equals("y3min")) {
			if (linerThreshold > linerThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("y3max") || type.equals("y3min")) {
			if (mVarianceEigenValueThreshold > mVarianceEigenValueThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("vevmax") || type.equals("vevmin")) {
			if (mVarianceEigenValueThreshold > mVarianceEigenValueThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("sigmaxmax") || type.equals("sigmaxmin")) {
			if (mSigmaValueThreshold > mSigmaValueThresholdEnd) {
				isFinish = true;
			}
		} else if (type.equals("sigmaymax") || type.equals("sigmaymin")) {
			if (mSigmaValueThreshold > mSigmaValueThresholdEnd) {
				isFinish = true;
			}
		}
	}

	/**
	 * タイプをコンストラクタで保持し、finishメソッドを統一
	 */
	@Override
	public boolean isFinish() {
		return isFinish;
	}

	/**
	 * 閾値をリセットする
	 */
	@Override
	public void resetThreshold() {
		isFinish = false;
		setValue();
	}

	// getter and setter

	public double getInnerthreshold() {
		return innerThreshold;
	}

	public void setInnerThreshold(double innerThreshold) {
		this.innerThreshold = innerThreshold;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getAngleThreshold() {
		return angleThreshold;
	}

	public void setAngleThreshold(double angleThreshold) {
		this.angleThreshold = angleThreshold;
	}

	public double getPerimeterThreshold() {
		return perimeterThreshold;
	}

	public void setPerimeterThreshold(double perimeterThreshold) {
		this.perimeterThreshold = perimeterThreshold;
	}

	public double getScoreThreshold() {
		return scoreThreshold;
	}

	public void setScoreThreshold(double scoreThreshold) {
		this.scoreThreshold = scoreThreshold;
	}

	public double getNormThreshold() {
		return normThreshold;
	}

	public void setNormThreshold(double normThreshold) {
		this.normThreshold = normThreshold;
	}

	public double getAspectThreshold() {
		return aspectThreshold;
	}

	public void setAspectThreshold(double aspectThreshold) {
		this.aspectThreshold = aspectThreshold;
	}

	public double getLinerThreshold() {
		return linerThreshold;
	}

	public void setLinerThreshold(double linerThreshold) {
		this.linerThreshold = linerThreshold;
	}

	public double getLinerThresholdEnd() {
		return linerThresholdEnd;
	}

	public void setLinerThresholdEnd(double linerThresholdEnd) {
		this.linerThresholdEnd = linerThresholdEnd;
	}

	public double getmVarianceEigenValueThreshold() {
		return mVarianceEigenValueThreshold;
	}

	public void setmVarianceEigenValueThreshold(double mVarianceEigenValueThreshold) {
		this.mVarianceEigenValueThreshold = mVarianceEigenValueThreshold;
	}

	public double getmSigmaValueThreshold() {
		return mSigmaValueThreshold;
	}

	public void setmSigmaValueThreshold(double mSigmaValueThreshold) {
		this.mSigmaValueThreshold = mSigmaValueThreshold;
	}

	/**
	 * 現在の設定を出力する
	 * 
	 * @return type 角度情報　周囲情報　内積情報　スコア情報　距離情報　アスペクト情報　線形情報　をタブ区切りで返す
	 */
	@Override
	public String output() {
		return type + "\t" + angleThreshold + "\t" + perimeterThreshold + "\t" + innerThreshold
				+ "\t" + scoreThreshold + "\t" + normThreshold + "\t" + aspectThreshold + "\t"
				+ linerThreshold + "\t" + mVarianceEigenValueThreshold + "\t"
				+ mSigmaValueThreshold;
	}

	/**
	 * 閾値をインポートする
	 */
	public void importThreshold(String thresholdStr) {
		if (thresholdStr == null) {
			return;
		}
		String[] thresholds = thresholdStr.split("\t");
		if (thresholds.length < 10) {
			return;
		}
		setType(thresholds[0]);
		setAngleThreshold(Double.parseDouble(thresholds[1]));
		setPerimeterThreshold(Double.parseDouble(thresholds[2]));
		setInnerThreshold(Double.parseDouble(thresholds[3]));
		setScoreThreshold(Double.parseDouble(thresholds[4]));
		setNormThreshold(Double.parseDouble(thresholds[5]));
		setAspectThreshold(Double.parseDouble(thresholds[6]));
		setLinerThreshold(Double.parseDouble(thresholds[7]));
		setmVarianceEigenValueThreshold(Double.parseDouble(thresholds[8]));
		setmSigmaValueThreshold(Double.parseDouble(thresholds[9]));
	}
}
