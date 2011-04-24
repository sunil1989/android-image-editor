package com.android.image.edit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class FileBitmap extends AbstractBitmapWrapper {
	
	public int backgroundColor;
	private Canvas canvas = new Canvas();
	private String bitmapFilePath;
	
	public FileBitmap(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public FileBitmap(int backgroundColor, String bitmapFilePath) {
		this.backgroundColor = backgroundColor;
		setBitmap(bitmapFilePath);
	}

	@Override
	public void drawPath(Path path, Paint paint) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = ImageEditorView.alphaBitmapConfig;
		Bitmap bm = BitmapFactory.decodeFile(bitmapFilePath, options);
		Bitmap bitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), ImageEditorView.alphaBitmapConfig);
		canvas.setBitmap(bitmap);
		canvas.drawBitmap(bm, 0, 0, null);
		bm.recycle();
		canvas.drawPath(path, paint);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(bitmapFilePath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (FileNotFoundException e) {
		}
		bitmap.recycle();
	}
	
	@Override
	public Bitmap getBitmap() {
		return loadBitmapFromFile(bitmapFilePath);
	}
	
	@Override
	public void setBitmap(String bitmapFilePath) {
		this.bitmapFilePath = bitmapFilePath;
	}

	@Override
	public void recycle(Bitmap bitmap) {
		bitmap.recycle();
	}

	@Override
	public boolean needMakeCopy() {
		return false;
	}

}
