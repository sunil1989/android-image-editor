package com.android.imgedit.command;

import android.graphics.Bitmap;
import android.graphics.RectF;
import com.android.imgedit.ImageEditorView;
import com.android.imgedit.ImageEditorView.ImageEditorViewMemento;

public class CropCommand extends MementoUndoableCommand<RectF, ImageEditorViewMemento, ImageEditorView> {

	public CropCommand(ImageEditorView target) {
		super(target);
	}

	@Override
	public void doExecute(RectF... params) {
		RectF selection = params[0];
		Bitmap croppedBitmap = Bitmap.createBitmap(target.getBitmap(), Math.round(selection.left), Math.round(selection.top), Math.round(selection.width()), Math.round(selection.height()));
		target.setBitmap(croppedBitmap);
		croppedBitmap.recycle();
	}
	
}
