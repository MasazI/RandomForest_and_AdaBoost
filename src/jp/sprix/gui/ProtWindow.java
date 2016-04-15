package jp.sprix.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import jp.sprix.adaboost.TrainCasePointData;
import jp.sprix.component.ComponentPointDoubleData;
import jp.sprix.component.ComponentPointIntData;
import jp.sprix.learning.data.CaseData;
import jp.sprix.learning.data.CoordinateCaseData;

/**
 * displayに点を打つ
 * 
 * @author root
 * 
 */
public class ProtWindow {
	public void displaySample(ComponentPointDoubleData componentPointData) {
		JFrame frame = new JFrame("train sample");
		frame.setBounds(0, 0, 500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(new PointPanel(componentPointData));

		frame.setVisible(true);
	}

	public void displaySample(ComponentPointIntData componentPointData) {
		JFrame frame = new JFrame("train sample");
		frame.setBounds(0, 0, 500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(new PointIntPanel(componentPointData));

		frame.setVisible(true);
	}

	public void displayQuery(ComponentPointIntData componentPointData, String title) {
		JFrame frame = new JFrame(title);
		frame.setBounds(0, 0, 500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(new PointIntPanel(componentPointData));

		frame.setVisible(true);
	}

	public void displayClassify(TrainCasePointData trainCasePointData, int[] finalClassify,
			ComponentPointDoubleData componentPointData) {
		JFrame frame = new JFrame("classify");
		frame.setBounds(0, 0, 500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(
				new ClassifyPointPanel(trainCasePointData, finalClassify, componentPointData));

		frame.setVisible(true);
	}
}

class ClassifyPointPanel extends JPanel {
	private int radius = 0;

	private Point2D.Double point1 = null;

	private Point2D.Double point2 = null;

	private CaseData[] caseDatas = null;

	private int[] finalClassify = null;

	public ClassifyPointPanel() {
		setBounds(0, 0, 1000, 1000);
	}

	public ClassifyPointPanel(TrainCasePointData trainCasePointData, int[] finalClassify,
			ComponentPointDoubleData componentPointData) {
		radius = componentPointData.getRadius();
		point1 = componentPointData.getCenter1();
		point2 = componentPointData.getCenter2();

		caseDatas = trainCasePointData.getCaseDatas();
		this.finalClassify = finalClassify;

		setBounds(0, 0, 1000, 1000);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int i = 0; i < caseDatas.length; i++) {
			CoordinateCaseData data = (CoordinateCaseData) caseDatas[i];
			ArrayList<Point2D.Double> points = data.getPointList();
			// TODO 複数点
			Point2D.Double point = points.get(0);

			if (finalClassify[i] == 1) {
				g.setColor(Color.BLUE);
				g.fillOval((int) point.getX(), (int) point.getY(), 2, 2);
			} else if (finalClassify[i] == -1) {
				g.setColor(Color.RED);
				g.fillOval((int) point.getX(), (int) point.getY(), 2, 2);
			} else if (finalClassify[i] == 0) {
				g.setColor(Color.ORANGE);
				g.fillOval((int) point.getX(), (int) point.getY(), 2, 2);
			}
		}
		g.setColor(Color.GRAY);
		g.drawOval((int) point1.getX() - radius, (int) point1.getY() - radius, 2 * radius,
				2 * radius);
		g.drawOval((int) point2.getX() - radius, (int) point2.getY() - radius, 2 * radius,
				2 * radius);
		g.setColor(Color.GREEN);
		g.fillOval((int) 500, (int) 500, 2, 2);
	}
}

class PointPanel extends JPanel {
	private HashMap<Point2D.Double, String> pointMap = null;

	private int radius = 0;

	private Point2D.Double point1 = null;

	private Point2D.Double point2 = null;

	public PointPanel() {
		setBounds(0, 0, 1000, 1000);
	}

	public PointPanel(ComponentPointDoubleData componentPointData) {
		pointMap = componentPointData.getSamples();
		radius = componentPointData.getRadius();
		point1 = componentPointData.getCenter1();
		point2 = componentPointData.getCenter2();

		setBounds(0, 0, 1000, 1000);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Set<Point2D.Double> points = pointMap.keySet();
		for (Point2D.Double point : points) {
			String type = pointMap.get(point);
			if (type.equals("+")) {
				g.setColor(Color.BLUE);
				g.fillOval((int) point.getX(), (int) point.getY(), 2, 2);
			} else {
				g.setColor(Color.RED);
				g.fillOval((int) point.getX(), (int) point.getY(), 2, 2);
			}
		}
		g.setColor(Color.GRAY);
		g.drawOval((int) point1.getX() - radius, (int) point1.getY() - radius, 2 * radius,
				2 * radius);
		g.drawOval((int) point2.getX() - radius, (int) point2.getY() - radius, 2 * radius,
				2 * radius);
		g.setColor(Color.GREEN);
		g.fillOval((int) 500, (int) 500, 2, 2);
	}
}

class PointIntPanel extends JPanel {
	private HashMap<Point, String> pointMap = null;

	private int radius = 0;

	private Point point1 = null;

	private Point point2 = null;

	public PointIntPanel() {
		setBounds(0, 0, 1000, 1000);
	}

	public PointIntPanel(ComponentPointIntData componentPointData) {
		pointMap = componentPointData.getSamples();
		radius = componentPointData.getRadius();
		point1 = componentPointData.getCenter1();
		point2 = componentPointData.getCenter2();

		setBounds(0, 0, 1000, 1000);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Set<Point> points = pointMap.keySet();
		for (Point point : points) {
			String type = pointMap.get(point);
			if (type.equals("+")) {
				g.setColor(Color.BLUE);
				g.fillOval((int) point.getX(), (int) point.getY(), 2, 2);
			} else {
				g.setColor(Color.RED);
				g.fillOval((int) point.getX(), (int) point.getY(), 2, 2);
			}
		}
		g.setColor(Color.GRAY);
		g.drawOval((int) point1.getX() - radius, (int) point1.getY() - radius, 2 * radius,
				2 * radius);
		g.drawOval((int) point2.getX() - radius, (int) point2.getY() - radius, 2 * radius,
				2 * radius);
		g.setColor(Color.GREEN);
		g.fillOval((int) 500, (int) 500, 2, 2);
	}
}