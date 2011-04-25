package com.android.image.edit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class MemoryBitmap extends AbstractBitmapWrapper {
	
	public int backgroundColor;
	private Canvas canvas = new Canvas();
	private Bitmap bitmap;
	private boolean originalBitmap;
	
	public MemoryBitmap(int backgroundColor, boolean originalBitmap) {
		this.backgroundColor = backgroundColor;
		this.originalBitmap = originalBitmap;
	}
	
	public MemoryBitmap(int backgroundColor, Bitmap bitmap, boolean originalBitmap, boolean needMakeCopy) {
		this.backgroundColor = backgroundColor;
		this.originalBitmap = originalBitmap;
		setBitmap(bitmap, needMakeCopy);
	}

	@Override
	public void drawPath(Path path, Paint paint) {
		canvas.drawPath(path, paint);
	}
	
	@Override
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	@Override
	public void setBitmap(String bitmapFilePath) {
		setBitmap(loadBitmapFromFile(bitmapFilePath), false);
	}
	
	public void setBitmap(Bitmap bitmap, boolean needMakeCopy) {
		if (this.bitmap != null) {
			this.bitmap.recycle();
		}
		if (originalBitmap) {
			this.bitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), ImageEditorView.alphaBitmapConfig);
			canvas.setBitmap(this.bitmap);
			canvas.drawBitmap(bitmap, 0, 0, null);
			if (!needMakeCopy) {
				bitmap.recycle();
			}
		} else {
			if (!bitmap.isMutable() || needMakeCopy) {
				this.bitmap = bitmap.copy(ImageEditorView.nonAlphaBitmapConfig, true);
				if (!needMakeCopy) {
					bitmap.recycle();
				}
				canvas.setBitmap(this.bitmap);
			} else {
				this.bitmap = bitmap;
				canvas.setBitmap(bitmap);
			}
		}
	}
	
	@Override
	public void setBitmap(Bitmap bitmap) {
		setBitmap(bitmap, false);
	}

	@Override
	public void recycle(Bitmap bitmap) {
	}

	@Override
	public boolean needMakeCopy() {
		return true;
	}

}
