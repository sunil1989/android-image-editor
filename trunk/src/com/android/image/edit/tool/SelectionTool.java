package com.android.image.edit.tool;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;

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
	public void touchStart(ImageEditorView context, float x, float y) {
		clearSelection();
		startX = x;
		startY = y;
		context.invalidate();
	}

	@Override
	public void touchMove(ImageEditorView context, float x, float y) {
		float left = Math.min(startX, x);
		float top = Math.min(startY, y);
		float right = Math.max(startX, x);
		float bottom = Math.max(startY, y);
		selection = new RectF(left, top, right, bottom);
		context.invalidate();
	}

	@Override
	public void touchUp(ImageEditorView context) {
		
	}

	@Override
	public void onDraw(ImageEditorView context, Canvas canvas) {
		canvas.drawColor(0xFFAAAAAA);

        context.drawTransformedBitmap(canvas);
        
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
