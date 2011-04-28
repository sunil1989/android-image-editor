package com.android.image.edit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public abstract class AbstractBitmapWrapper implements BitmapWrapper {
	
	protected Paint paint;
	
	public AbstractBitmapWrapper(boolean originalBitmap) {
		paint = createPaint(originalBitmap);
	}

	protected Bitmap loadBitmapFromFile(String bitmapFilePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = ImageEditorView.nonAlphaBitmapConfig;
		return BitmapFactory.decodeFile(bitmapFilePath, options);
	}
	
	private Paint createPaint(boolean originalBitmap) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(ImageEditorView.BACKGROUND_COLOR);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		if (originalBitmap) {
			paint.setXfermode(new PorterDuffXfermode(
					PorterDuff.Mode.CLEAR));
		}
		return paint;
	}
	
}
