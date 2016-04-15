package jp.sprix.test2;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import static org.junit.Assert.*;

import org.junit.Test;
import jp.sprix.sample.Sampling;

public class SprixTest2 {
	@Test
	public void test() {
		int a = 2;
		int b = 3;
		
		int c = a + b;
		assertThat(c, is(5));
		
		
		int max = 100;
		Sampling sample = new Sampling(2);
		int random = sample.sampling(max);
		
		assertThat(c, not(101));
		System.out.println(random);
	}

}
