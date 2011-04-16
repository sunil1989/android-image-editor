package com.android.image.edit.tool;

import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.view.MotionEvent;

import com.android.image.edit.ImageEditorView;

public class DrawRectSelectionTool extends AbstractSelectionTool {
	
	private float startX, startY;
	
	public DrawRectSelectionTool(Style paintStyle) {
		super(0xFF000000);
	}

	@Override
	public void touchStart(ImageEditorView context, MotionEvent event) {
		clearSelection();
		startX = event.getX();
		startY = event.getY();
		context.invalidate();
	}

	@Override
	public void touchMove(ImageEditorView context, MotionEvent event) {
		float x = event.getX(),
		y = event.getY();
		float left = Math.min(startX, x),
		top = Math.min(startY, y),
		right = Math.max(startX, x),
		bottom = Math.max(startY, y);
		selection = new RectF(left, top, right, bottom);
		context.invalidate();
	}

	@Override
	public void touchUp(ImageEditorView context) {}
	
}
