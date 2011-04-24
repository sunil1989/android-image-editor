package com.android.image.edit;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;

public interface BitmapWrapper {
	
	void drawPath(Path path, Paint paint);
	
	Bitmap getBitmap();
	
	void setBitmap(String bitmapFilePath);
	
	void recycle(Bitmap bitmap);
	
	boolean needMakeCopy();

}
