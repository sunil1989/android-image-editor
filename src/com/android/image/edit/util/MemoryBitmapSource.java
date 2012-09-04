package com.android.image.edit.util;

import android.graphics.Bitmap;

public class MemoryBitmapSource implements Source<Bitmap> {
	
	private Bitmap bitmap;

	public MemoryBitmapSource(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public Bitmap get() {
		return bitmap;
	}

}
