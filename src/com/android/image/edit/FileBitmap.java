package com.android.image.edit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.android.image.edit.memento.Memento;
import com.android.image.edit.memento.MementoOriginator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class FileBitmap implements BitmapWrapper, MementoOriginator<FileBitmap.CanvasBitmapMemento> {
	
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
		Bitmap bitmap = getBitmap();
		canvas.drawPath(path, paint);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(bitmapFilePath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (FileNotFoundException e) {
		}
		bitmap.recycle();
	}

	public Bitmap getBitmap() {
		Bitmap bm = BitmapFactory.decodeFile(bitmapFilePath);
		Bitmap bitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
		canvas.setBitmap(bitmap);
		canvas.drawBitmap(bm, 0, 0, null);
		//canvas.setBitmap(bm);
		//bm.recycle();
		return bitmap;
		//return bm;
	}
	
	public Bitmap getPlainBitmap() {
		Bitmap bm = BitmapFactory.decodeFile(bitmapFilePath);
		return bm;
	}

	public String getBitmapFilePath() {
		return bitmapFilePath;
	}

	public Canvas getCanvas() {
		return canvas;
	}
	
	public void setBitmap(String bitmapFilePath) {
		this.bitmapFilePath = bitmapFilePath;
		/*if (this.bitmap != null && bitmap.getWidth() == this.bitmap.getWidth() && bitmap.getHeight() == this.bitmap.getHeight()) {
			clearCanvas();
			canvas.drawBitmap(bitmap, 0, 0, null);
			return;
		}
		if (this.bitmap != null) {
			this.bitmap.recycle();
		}
		this.bitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		canvas.setBitmap(this.bitmap);
		canvas.drawBitmap(bitmap, 0, 0, null);*/
	}
	
	/*private void clearCanvas() {
    	canvas.drawColor(backgroundColor);
    }*/
	
	public CanvasBitmapMemento createMemento() {
		return new CanvasBitmapMemento(getBitmap().copy(Bitmap.Config.ARGB_8888, false));
	}
	
	public void setMemento(CanvasBitmapMemento memento) {
		//setBitmap(memento.state);
		//setOriginalAndUpdateTransformedBitmap(memento.state);
		//invalidate();
	}
	
	/*public void recycle() {
		bitmap.recycle();
	}*/
	
	public static class CanvasBitmapMemento implements Memento {
		
		private Bitmap state;

		private CanvasBitmapMemento(Bitmap state) {
			this.state = state;
		}
		
		public void recycle() {
			state.recycle();
		}
		
	}

}
