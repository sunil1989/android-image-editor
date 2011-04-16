package com.android.image.edit.tool;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;

import com.android.image.edit.ImageEditorView;
import com.android.image.edit.command.CropCommand;

public abstract class AbstractSelectionTool implements Tool {
	
	protected RectF selection = new RectF();
	private Paint fillPaint;
	private Paint rectPaint;

	public AbstractSelectionTool(int rectPaintColor, int fillPaintColor) {
		this(rectPaintColor);
		fillPaint = new Paint();
		fillPaint.setStyle(Style.FILL);
		fillPaint.setColor(fillPaintColor);
	}
	
	public AbstractSelectionTool(int rectPaintColor) {
		rectPaint = new Paint();
		rectPaint.setStyle(Style.STROKE);
		rectPaint.setColor(rectPaintColor);
	}

	@Override
	public void onDraw(ImageEditorView context, Canvas canvas) {
        if (selection != null) {
        	canvas.drawRect(selection, rectPaint);
        	if (fillPaint != null) {
        		canvas.drawRect(selection, fillPaint);
        	}
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
