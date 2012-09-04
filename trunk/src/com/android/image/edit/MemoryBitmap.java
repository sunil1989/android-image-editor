package com.android.image.edit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region.Op;

public class MemoryBitmap extends AbstractBitmapWrapper {
	
	private Canvas canvas = new Canvas();
	private Bitmap bitmap;
	private boolean original;
	private ImageEditorView context;
	
	public MemoryBitmap(ImageEditorView context, boolean original) {
		super(original);
		this.original = original;
		this.context = context;
	}
	
	public MemoryBitmap(ImageEditorView context, Bitmap bitmap, boolean original) {
		this(context, original);
		setBitmap(bitmap, null);
	}

	@Override
	public void drawPath(Path path, float strokeWidth) {
		pathPaint.setStrokeWidth(strokeWidth);
		canvas.drawPath(path, pathPaint);
	}
	
	@Override
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	@Override
	public void setBitmap(String bitmapFilePath) {
		setBitmap(loadBitmapFromFile(bitmapFilePath), null);
	}

	@Override
	public void setBitmap(Bitmap bitmap, Matrix transform) {
		int newBitmapWidth, newBitmapHeight;
		if (transform == null) {
			newBitmapWidth = bitmap.getWidth();
			newBitmapHeight = bitmap.getHeight();
			if (!original && transform == null) {
				context.log.append("Image width: ").append(newBitmapWidth).append(", image height: ").append(newBitmapHeight).append("\n");
			}
		} else {
			RectF r = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
			transform.mapRect(r);
			newBitmapWidth = Math.round(r.right);
			newBitmapHeight = Math.round(r.bottom);
		}
		if (this.bitmap == null || newBitmapWidth != this.bitmap.getWidth() || newBitmapHeight != this.bitmap.getHeight()) {
		if (this.bitmap != null) {
			this.bitmap.recycle();
		}
			this.bitmap = Bitmap.createBitmap(newBitmapWidth, newBitmapHeight, original ? ImageEditorView.ORIGINAL_BITMAP_CONFIG : ImageEditorView.DISPLAYABLE_BITMAP_CONFIG);
			canvas.setBitmap(this.bitmap);
			canvas.clipRect(0, 0, canvas.getWidth(), canvas.getHeight(), Op.REPLACE);
			if (!original) {
				canvas.drawColor(ImageEditorView.BACKGROUND_COLOR);
			}
				}
		if (transform == null || transform.isIdentity()) {
			canvas.drawBitmap(bitmap, 0, 0, null);
			} else {
			canvas.drawBitmap(bitmap, transform, null);
			}
		}

	@Override
	public boolean isOriginal() {
		return original;
	}

}
