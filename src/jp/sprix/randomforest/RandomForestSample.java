package jp.sprix.randomforest;

public class RandomForestSample {
	/**
	 * 木の数
	 */
	private int treeCnt = 0;

	/**
	 * 木の配列
	 */
	private RandomForestTree[] randomForestTreeArray = null;

	/**
	 * validationの結果を木の数だけ保存
	 */
	private int[][] validationResult = null;

	/**
	 * コンストラクタ
	 * 
	 * @param treeCnt
	 */
	public RandomForestSample(int treeCnt) {
		this.treeCnt = treeCnt;
		randomForestTreeArray = new RandomForestTree[treeCnt];
	}

	/**
	 * 学習して木の配列を作成する
	 * 
	 * @param sampleCase
	 *            全事例オブジェクト
	 * @param randomForestSampling
	 *            学習するサンプリング集合（事例の添字）
	 */
	public void train(RandomForestSampleCase sampleCase, RandomForestSampling randomForestSampling) {
		for (int i = 0; i < treeCnt; i++) {
			System.out.println((i + 1) + "本目");
			RandomForestTree tree = new RandomForestTree(i, randomForestSampling.getSampling(i),
					sampleCase);
			tree.makeTree();
			randomForestTreeArray[i] = tree;

			// Thread th = new Thread(tree);
			// th.start();
		}
	}

	/**
	 * クエリのバリデーションを行う
	 * 
	 * @param sampleCase
	 */
	public void validation(RandomForestSampleCase sampleCase) {
		// 木の数だけ結果を保持する
		validationResult = new int[treeCnt][];
		for (int i = 0; i < randomForestTreeArray.length; i++) {
			// 木
			validationResult[i] = randomForestTreeArray[i].validation(sampleCase);
		}

		// 投票結果をsampleCaseオブジェクトに登録
		int[] votingResult = new int[sampleCase.cases.length];
		for (int i = 0; i < votingResult.length; i++) {
			int votingCnt = 0;
			for (int j = 0; j < validationResult.length; j++) {
				votingCnt += validationResult[j][i];

			}
			if (votingCnt > 0) {
				votingResult[i] = 1;
			} else {
				votingResult[i] = -1;
			}
		}

		sampleCase.positiveOrNegative = votingResult;
	}
}
