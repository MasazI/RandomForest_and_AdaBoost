package jp.sprix.cv;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 画像クラス
 * 
 * @author root
 * 
 */
public class CVImage {
	public static Image loadImage(File file) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			System.out.println("[error] can not load image. " + e.getMessage());
		}
		return image;
	}
}
