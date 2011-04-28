package com.android.image.edit;

import android.graphics.Bitmap;
import android.graphics.Path;

public interface BitmapWrapper {
	
	void drawPath(Path path, float strokeWidth);
	
	Bitmap getBitmap();
	
	void setBitmap(String bitmapFilePath);
	
	void setBitmap(Bitmap bitmap);
	
	void recycle(Bitmap bitmap);
	
	boolean needMakeCopy();

}
