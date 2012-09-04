package com.android.image.edit.scroll;

import com.android.image.edit.ImageEditorView;

public class ImageScrollStateFactory {
	
	private static ImageScrollStateFactory instance;
	
	private ImageScrollStateFactory() {}
	
	public static ImageScrollStateFactory getInstance() {
		if (instance == null) {
			instance = new ImageScrollStateFactory();
		}
		return instance;
	}

	public ImageScrollState createImageScrollState(int imageWidth, int imageHeight, int viewWidth, int viewHeight, ImageEditorView context) {
		if ((imageWidth <= viewWidth && imageHeight <= viewHeight) ||
				(imageWidth <= viewHeight && imageHeight <= viewWidth)) {
			return new ImageNoScrollState(imageWidth, imageHeight, viewWidth, viewHeight);
		} else {
			return new ImageScrollPresentState(imageWidth, imageHeight, viewWidth, viewHeight);
		}
	}

}
