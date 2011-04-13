package com.android.image.edit.tool;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.view.MotionEvent;

import com.android.image.edit.ImageEditorView;
import com.android.image.edit.command.CropCommand;

public class SelectionTool implements Tool {
	
	private float startX, startY;
	private RectF selection;
	private Paint mPaint;

	public SelectionTool() {
		mPaint = new Paint();
		mPaint.setStyle(Style.STROKE);
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
		y = event.getY(),
		left = Math.min(startX, x),
		top = Math.min(startY, y),
		right = Math.max(startX, x),
		bottom = Math.max(startY, y);
		selection = new RectF(left, top, right, bottom);
		context.invalidate();
	}

	@Override
	public void touchUp(ImageEditorView context) {
		
	}

	@Override
	public void onDraw(ImageEditorView context, Canvas canvas) {
        if (selection != null) {
        	canvas.drawRect(selection, mPaint);
        }
	}
	
	public RectF clearSelection() {
		RectF selectionCopy = selection;
		selection = null;
		return selectionCopy;
	}

	@Override
	public void drawPath(ImageEditorView context) {}
	
	@Override
	public void crop(ImageEditorView context) {
		if (selection != null) {
			RectF originalSelection = context.getOriginalRect(clearSelection());
			context.getCommandManager().executeCommand(new CropCommand(context), originalSelection);
		}
	}
	
}
