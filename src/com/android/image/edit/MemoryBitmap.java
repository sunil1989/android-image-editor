package com.android.image.edit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class MemoryBitmap implements BitmapWrapper {
	
	public int backgroundColor;
	
	private Canvas canvas = new Canvas();
	private Bitmap bitmap;
	
	public MemoryBitmap(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public MemoryBitmap(int backgroundColor, Bitmap bitmap) {
		this.backgroundColor = backgroundColor;
		setBitmap(bitmap);
	}

	@Override
	public void drawPath(Path path, Paint paint) {
		canvas.drawPath(path, paint);
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public Canvas getCanvas() {
		return canvas;
	}
	
	public void setBitmap(Bitmap bitmap) {
		if (this.bitmap != null && bitmap.getWidth() == this.bitmap.getWidth() && bitmap.getHeight() == this.bitmap.getHeight()) {
			clearCanvas();
			canvas.drawBitmap(bitmap, 0, 0, null);
			return;
		}
		if (this.bitmap != null) {
			this.bitmap.recycle();
		}
		this.bitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		canvas.setBitmap(this.bitmap);
		canvas.drawBitmap(bitmap, 0, 0, null);
	}
	
	private void clearCanvas() {
    	canvas.drawColor(backgroundColor);
    }
	
	public void recycle() {
		bitmap.recycle();
	}

}
