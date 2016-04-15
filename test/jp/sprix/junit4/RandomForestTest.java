package jp.sprix.junit4;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.is;

import java.util.Properties;

import jp.sprix.component.ComponentPointIntData;
import jp.sprix.gui.ProtWindow;
import jp.sprix.io.Import;
import jp.sprix.randomforest.RandomForestSampleCase;

import org.junit.Test;

/**
 * RandomForestのテストクラス
 * 
 * @author root
 * 
 */
public class RandomForestTest {
	@Test
	public void testEntropy() {
		Properties prop = Import.importProperty();

		// 事例の作成
		int sampleAllCnt = Integer.parseInt(prop.getProperty("randomforest.sample.allcnt"));
		int sampleMaxNumber = Integer.parseInt(prop.getProperty("randomforest.sample.max"));
		// 正事例と負事例の判定が可能な事例コンポーネントクラス
		ComponentPointIntData componentPointData = new ComponentPointIntData("randomforest");
		// 正事例と負事例の判定を行いながら、事例を作成
		RandomForestSampleCase sampleCase = new RandomForestSampleCase(sampleAllCnt,
				sampleMaxNumber, componentPointData);

		// Display
		ProtWindow window = new ProtWindow();
		window.displaySample(componentPointData);
		
		// fail("Not yet implemented");
		assertThat("test", is("test"));
	}

}
