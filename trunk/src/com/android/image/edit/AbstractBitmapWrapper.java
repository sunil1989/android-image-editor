package com.android.image.edit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class AbstractBitmapWrapper implements BitmapWrapper {
	
	protected Bitmap loadBitmapFromFile(String bitmapFilePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = ImageEditorView.nonAlphaBitmapConfig;
		return BitmapFactory.decodeFile(bitmapFilePath, options);
	}
	
}
