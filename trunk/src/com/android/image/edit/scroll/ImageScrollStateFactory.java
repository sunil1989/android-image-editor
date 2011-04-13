package com.android.image.edit.scroll;

public class ImageScrollStateFactory {
	
	private static ImageScrollStateFactory instance;
	
	private ImageScrollStateFactory() {}
	
	public static ImageScrollStateFactory getInstance() {
		if (instance == null) {
			instance = new ImageScrollStateFactory();
		}
		return instance;
	}

	public ImageScrollState createImageScrollState(boolean imageFitToView) {
		if (imageFitToView) {
			return ImageNoScrollState.getInstance();
		} else {
			return new ImageScrollPresentState();
		}
	}

}
