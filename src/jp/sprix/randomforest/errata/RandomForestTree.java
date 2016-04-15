package jp.sprix.randomforest.errata;

import java.util.Properties;

import jp.sprix.io.Import;
import jp.sprix.io.Output;
import jp.sprix.sample.Sampling;

/**
 * ランダムフォレストの木クラス
 * 
 * 他の木と並列に処理を行う
 * 
 * @author root
 * 
 */
public class RandomForestTree {
	private int treeNum = 0;

	private int[] samplingNum = null;

	private RandomForestErrataCase allSampleCase = null;

	private Properties prop = null;

	private Sampling sampling = null;

	RandomForestNode node = null;

	/**
	 * コンストラクタ
	 * 
	 * @param treeNum
	 *            木の番号
	 * @param samplingNum
	 *            この木で使用するサンプリング番号
	 * @param allSampleCase
	 *            全事例
	 */
	public RandomForestTree(int treeNum, int[] samplingNum, RandomForestErrataCase allSampleCase) {
		this.treeNum = treeNum;
		this.samplingNum = samplingNum;
		this.allSampleCase = allSampleCase;
		prop = Import.importProperty();
		// サンプリング用の数値はtreeNumに依存させる
		sampling = new Sampling(Integer.parseInt(prop.getProperty("randomforest.sampling.seed"))
				+ treeNum);
	}

	/**
	 * 木を作成する（トップノード）
	 */
	public void makeTree() {
		// トップノードの作成
		node = getMinEntropyNode(true, samplingNum);
		StringBuffer sb = new StringBuffer();
		makeTree(true, samplingNum, node, "", sb);
		// ファイル出力
		Output.outputFile(prop.getProperty("randomforest.errata.dictionary.dir") + treeNum + ".tsv", sb);
	}

	/**
	 * 木を作成する(回帰)
	 * 
	 * @param isTopNode
	 *            初回のみtrueで呼び出し、falseで回帰する
	 */
	private void makeTree(boolean isTopNode, int[] samplingNumForNode, RandomForestNode node,
			String paragraph, StringBuffer sb) {

		// 分類された事例がすべて同じ方
		if (allSampleCase.isAllPositive(samplingNumForNode)) {
			node.setLeaf(true);
			node.setCategory(1);
			// debug(選択されたノード文字列を出力)
			System.out.println(paragraph + node.output());
			sb.append(paragraph + node.output());
			sb.append("\n");
			return;
		} else if (allSampleCase.isAllNegative(samplingNumForNode)) {
			node.setLeaf(true);
			node.setCategory(-1);
			// debug(選択されたノード文字列を出力)
			System.out.println(paragraph + node.output());
			sb.append(paragraph + node.output());
			sb.append("\n");
			return;
		} else {
			// debug(選択されたノード文字列を出力)
			System.out.println(paragraph + node.output());
			sb.append(paragraph + node.output());
			sb.append("\n");
		}

		// 子ノード用分離node後のサンプリング集合添字配列
		int[] samplingL = node.getSamplingL(samplingNumForNode, allSampleCase);
		int[] samplingR = node.getSamplingR(samplingNumForNode, allSampleCase);

		// debug
		// int lCnt = samplingL.length;
		// int rCnt = samplingR.length;

		int negativeCntL = allSampleCase.negativeCnt(samplingL);
		int positiveCntL = allSampleCase.positiveCnt(samplingL);
		int negativeCntR = allSampleCase.negativeCnt(samplingR);
		int positiveCntR = allSampleCase.positiveCnt(samplingR);

		// 一方にしか分類されない場合
		boolean isLEnd = false;
		boolean isREnd = false;

		// 片方にしか分類されない場合は
		if (samplingL.length == 0) {
			node.setLeaf(true);
			if (negativeCntL < positiveCntL) {
				node.setCategory(1);
			} else {
				node.setCategory(-1);
			}
			isLEnd = true;
			// debug(選択されたノード文字列を出力)
			System.out.println(paragraph + node.output());
			sb.append(paragraph + node.output());
			sb.append("\n");
			return;
		} else if (samplingR.length == 0) {
			node.setLeaf(true);
			if (negativeCntR < positiveCntR) {
				node.setCategory(1);
			} else {
				node.setCategory(-1);
			}
			isREnd = true;
			// debug(選択されたノード文字列を出力)
			System.out.println(paragraph + node.output());
			sb.append(paragraph + node.output());
			sb.append("\n");
			return;
		}

		if (!isLEnd) { // Lに分類されたものが無ければLの枝はなし(終端では無い)
			RandomForestNode childeNodeL = getMinEntropyNode(isTopNode, samplingL);
			// 子ノードをセット
			node.setChildLeftNode(childeNodeL); // 子ノードの枝を作成
			makeTree(false, samplingL, childeNodeL, paragraph + "\t", sb);
		}
		if (!isREnd) {
			// Rに分類されたものが無ければRの枝はなし(終端では無い)
			RandomForestNode childeNodeR = getMinEntropyNode(isTopNode, samplingR);
			// 子ノードをセット
			node.setChildRightNode(childeNodeR); // 子ノードの枝を作成
			makeTree(false, samplingR, childeNodeR, paragraph + "\t", sb);
		}
	}

	/**
	 * エントロピーが最大になるノードの文字列表現を返す
	 * 
	 * @param isTopNode
	 * @return
	 */
	private RandomForestNode getMinEntropyNode(boolean isTopNode, int[] samplingNumByNode) {
		// 分離関数候補を取得する
		String[] nodeTypeCandidate = getNodeTypeCandidate();

		// カウント
		int cnt = 0;
		// 最大のエントロピー差
		double maxEntropyDiff = 0;
		// 最大のエントロピーをもつ分離Nodeの文字列表現
		String maxEntropyDiffNodeStr = null;

		// 分離Nodo候補でループ
		for (String nodeType : nodeTypeCandidate) {
			// 指定した分離関数でノード候補を作成
			RandomForestNode forestNode = new RandomForestNode(prop, nodeType, isTopNode);
			// 分離関数内で閾値を変化させて、最大のエントロピーを取得する
			while (!forestNode.isFinish()) {
				double entropyDiff = RandomForestEntropy.getEntropyDiff(samplingNumByNode,
						allSampleCase, forestNode);
				// 最大エントロピーと分離ノードの更新
				if (cnt == 0) {
					maxEntropyDiff = entropyDiff;
					maxEntropyDiffNodeStr = forestNode.output();
				} else {
					if (maxEntropyDiff < entropyDiff) {
						maxEntropyDiff = entropyDiff;
						maxEntropyDiffNodeStr = forestNode.output();
					}
				}
				// 次の閾値へ
				forestNode.nextThreshold();
				if (!Double.isNaN(maxEntropyDiff)) {
					cnt++;
				}
			}
		}

		RandomForestNode forestNode = new RandomForestNode();
		forestNode.importThreshold(maxEntropyDiffNodeStr);
		return forestNode;
	}

	/**
	 * Node候補の特徴をサンプリングする
	 * 
	 * @return
	 */
	private String[] getNodeTypeCandidate() {
		// サンプリング個数
		int nodeTypeCnt = Math.round(Math.round(Math.sqrt(NodeType.nodeTypes.length)));

		if (nodeTypeCnt > NodeType.nodeTypes.length) {
			nodeTypeCnt = NodeType.nodeTypes.length;
		}

		// サンプリング結果の入れ物
		String[] nodeTypeCandidate = new String[nodeTypeCnt];

		// サンプリング
		for (int i = 0; i < nodeTypeCnt; i++) {
			// System.out.println(sampling.sampling(NodeType.nodeTypes.length));
			nodeTypeCandidate[i] = NodeType.nodeTypes[sampling.sampling(NodeType.nodeTypes.length)];
		}
		return nodeTypeCandidate;
	}

	/**
	 * サンプルケースを分類する
	 * 
	 * @param allSampleCase
	 */
	public int[] validation(RandomForestErrataCase allSampleCase) {
		int[][] features = allSampleCase.features;
		int[] positiveOrNegative = new int[features.length];

		for (int i = 0; i < features.length; i++) {
			int[] feature = features[i];
			if (node.isAdaptiveFeature(feature) > 0) {
				positiveOrNegative[i] = search(feature, node.getChildLeftNode());
			} else {
				positiveOrNegative[i] = search(feature, node.getChildRightNode());
			}
		}
		return positiveOrNegative;
	}

	/**
	 * 特徴量とnodeを使用して、木を末端まで探索する(回帰)
	 * 
	 * @param feature
	 *            特徴量
	 * @param node
	 *            ノード
	 * @return カテゴリーを返す
	 */
	private int search(int[] feature, RandomForestNode node) {
		if (node.isLeaf()) {
			return node.getCategory();
		} else {
			if (node.isAdaptiveFeature(feature) > 0) {
				return search(feature, node.getChildLeftNode());
			} else {
				return search(feature, node.getChildRightNode());
			}
		}
	}
}
