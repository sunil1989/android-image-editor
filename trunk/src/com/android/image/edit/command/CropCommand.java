package com.android.image.edit.command;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.android.image.edit.BitmapWrapper;

public class CropCommand extends AbstractImageEditCommand {

	public CropCommand() {
		super(false, false);
	}

	@Override
	public void execute(BitmapWrapper target, Object... params) {
		RectF selection = (RectF)params[0];
		Bitmap bitmap = target.getBitmap();
		int x = Math.round(selection.left);
		int y = Math.round(selection.top);
		int width = Math.min(Math.round(selection.width()), bitmap.getWidth() - x);
		int height = Math.min(Math.round(selection.height()), bitmap.getHeight() - y);
		Bitmap croppedBitmap = Bitmap.createBitmap(target.getBitmap(), x, y, width, height);
		target.setBitmap(croppedBitmap, null);
	}
	
}
