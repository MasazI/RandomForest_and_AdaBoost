package jp.sprix.junit4;

import java.util.ArrayList;

import jp.sprix.cv.CVErrataGeometry;

import org.junit.Test;

public class CVErrataGeometryTest {
	@Test
	public void testCoordinateVarianceCovarianceMatrix() {
		double[] c1 = { 3, 2 };
		double[] c2 = { 3, 4 };
		double[] c3 = { 5, 4 };
		double[] c4 = { 5, 6 };

		ArrayList<double[]> coordinates = new ArrayList<>();
		coordinates.add(c1);
		coordinates.add(c2);
		coordinates.add(c3);
		coordinates.add(c4);

		CVErrataGeometry.getCoordinateVarianceCovarianceMatrix(coordinates);

	}
}
