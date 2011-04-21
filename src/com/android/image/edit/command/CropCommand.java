package com.android.image.edit.command;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.android.image.edit.BitmapWrapper;
import com.android.image.edit.FileBitmap;

public class CropCommand extends AbstractMultiTargetCommand<BitmapWrapper> {

	public CropCommand(BitmapWrapper target) {
		super(target);
	}

	@Override
	public void execute(BitmapWrapper target, Object... params) {
		if (!(params[0] instanceof RectF)) {
			throw new IllegalArgumentException("android.graphics.RectF expected but was " + params[0].getClass());
		}
		RectF selection = (RectF)params[0];
		//Bitmap croppedBitmap = Bitmap.createBitmap(target.getBitmap(), Math.round(selection.left), Math.round(selection.top), Math.round(selection.width()), Math.round(selection.height()));
		//target.setBitmap(croppedBitmap);
		//croppedBitmap.recycle();
	}
	
}
