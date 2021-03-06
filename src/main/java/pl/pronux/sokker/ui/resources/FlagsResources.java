package pl.pronux.sokker.ui.resources;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import pl.pronux.sokker.ui.handlers.DisplayHandler;

public final class FlagsResources {

	public static final int EMPTY_FLAG = 500;
	public static final int QUESTION_FLAG = 501;
	private static final String IMAGE_PATH = "/flags/"; 

	private static Map<Integer, Image> cache = new HashMap<Integer, Image>();

	static {
		DisplayHandler.getDisplay().disposeExec(new Runnable() {
			public void run() {
				if (!cache.isEmpty()) {
					for (Image image : cache.values()) {
						if (!image.isDisposed()) {
							image.dispose();
						}
					}
				}
			}
		});
	}

	private FlagsResources() {
	}

	public static Image getFlag(int idCountry) {
		Image image = cache.get(idCountry);
		if (image == null || image.isDisposed()) {
			image = loadImage(idCountry + ".png"); 
			if (image == null) {
				image = getFlag(QUESTION_FLAG);
			}
			cache.put(idCountry, image);
		}
		return image;
	}

	public static Image getFlagLight(int idCountry) {
		Image image;
		image = cache.get(-idCountry);
		if (image == null || image.isDisposed()) {
			image = loadImageLight(idCountry + ".png"); 
			if (image == null) {
				image = getFlagLight(QUESTION_FLAG);
			}
			cache.put(-idCountry, image);
		}
		return image;
	}

	public static Image getFlagVeryLight(int idCountry) {
		Image image = cache.get(-idCountry);
		
		if (image == null || image.isDisposed()) {
			image = loadImageVeryLight(idCountry + ".png"); 
			if (image == null) {
				image = getFlagVeryLight(QUESTION_FLAG);
			}
			cache.put(-idCountry, image);
		}
		return image;
	}

	private static Image loadImageLight(String filename) {
		Image image = null;
		InputStream is = FlagsResources.class.getResourceAsStream(IMAGE_PATH + filename);
		if (is != null) {
			ImageData id = new ImageData(is);
			if (id != null) {
				for (int y = 0; y < id.height; y++) {
					for (int x = 0; x < id.width; x++) {
						id.setAlpha(x, y, 128);
					}
				}
				image = new Image(DisplayHandler.getDisplay(), id);
			}
		}
		return image;
	}

	private static Image loadImageVeryLight(String filename) {
		Image image = null;
		InputStream is = FlagsResources.class.getResourceAsStream(IMAGE_PATH + filename);
		if (is != null) {
			ImageData id = new ImageData(is);
			if (id != null) {
				for (int y = 0; y < id.height; y++) {
					for (int x = 0; x < id.width; x++) {
						id.setAlpha(x, y, 64);
					}
				}
				image = new Image(DisplayHandler.getDisplay(), id);
			}
		}
		return image;
	}

	private static Image loadImage(String filename) {
		Image image = null;
		InputStream is = FlagsResources.class.getResourceAsStream(IMAGE_PATH + filename);
		if (is != null) {
			image = new Image(DisplayHandler.getDisplay(), is);
		}
		return image;
	}

}
