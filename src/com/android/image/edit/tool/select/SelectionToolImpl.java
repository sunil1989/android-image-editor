package com.android.image.edit.tool.select;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import com.android.image.edit.ImageEditorView;
import com.android.image.edit.R;

public class SelectionToolImpl implements SelectionTool {

	private static float INIT_SELECTION_SIZE_PART = 0.8f;
	private static int SELECTION_RECT_PAINT_COLOR = 0xFF000000;
	private static int SELECTION_RECT_FILL_COLOR = 0x70FFFFFF;
	private static final int TOUCH_TOLERANCE = 50;
	private static final int MIN_SELECTION_RECT_WIDTH = 3*TOUCH_TOLERANCE;
	private static final int MIN_SELECTION_RECT_HEIGHT = 3*TOUCH_TOLERANCE;
	private int fixedVerticalEdge, fixedHorizontalEdge;
	private boolean rectVertexSelected = false;
	private boolean rectSelected = false;
	private int xStart, yStart;
	private Rect initialSelection = new Rect();
	private boolean toRightOfFixed, toBottomOfFixed;
	
	protected Rect selection;
	private Paint fillPaint;
	private Paint rectPaint;
	private Bitmap leftBottom, leftTop, rightBottom, rightTop;

	public SelectionToolImpl(ImageEditorView context) {
		rectPaint = new Paint();
		rectPaint.setStyle(Style.STROKE);
		rectPaint.setColor(SELECTION_RECT_PAINT_COLOR);
		fillPaint = new Paint();
		fillPaint.setStyle(Style.FILL);
		fillPaint.setColor(SELECTION_RECT_FILL_COLOR);
		leftBottom = BitmapFactory.decodeResource(context.getContext().getResources(), R.drawable.crop_left_bottom);
		leftTop = BitmapFactory.decodeResource(context.getContext().getResources(), R.drawable.crop_left_top);
		rightBottom = BitmapFactory.decodeResource(context.getContext().getResources(), R.drawable.crop_right_bottom);
		rightTop = BitmapFactory.decodeResource(context.getContext().getResources(), R.drawable.crop_right_top);
		Rect imageVisibleRegionBounds = context.getImageVisibleRegionBounds();
		int selectionRectWidth = Math.max(Math.round(INIT_SELECTION_SIZE_PART*imageVisibleRegionBounds.width()), MIN_SELECTION_RECT_WIDTH);
		int selectionRectHeight = Math.max(Math.round(INIT_SELECTION_SIZE_PART*imageVisibleRegionBounds.height()), MIN_SELECTION_RECT_HEIGHT);
		int leftIndent = (imageVisibleRegionBounds.width() - selectionRectWidth)/2;
		int topIndent = (imageVisibleRegionBounds.height() - selectionRectHeight)/2;
		selection = new Rect(imageVisibleRegionBounds.left+leftIndent, imageVisibleRegionBounds.top+topIndent,
				imageVisibleRegionBounds.left+leftIndent+selectionRectWidth, imageVisibleRegionBounds.top+topIndent+selectionRectHeight);
		context.invalidate();
	}
	
	@Override
	public void onDraw(ImageEditorView context, Canvas canvas) {
        if (selection != null) {
        	canvas.drawRect(selection, rectPaint);
        	canvas.drawRect(selection, fillPaint);
        	canvas.drawBitmap(leftBottom, selection.left, selection.bottom-leftBottom.getHeight(), null);
        	canvas.drawBitmap(leftTop, selection.left, selection.top, null);
        	canvas.drawBitmap(rightBottom, selection.right-rightBottom.getWidth(), selection.bottom-rightBottom.getHeight(), null);
        	canvas.drawBitmap(rightTop, selection.right-rightTop.getWidth(), selection.top, null);
        }
	}
	
	private boolean pointsAreClose(float x1, float y1, float x2, float y2) {
		return Math.hypot(x1-x2, y1-y2) < TOUCH_TOLERANCE;
	}
	
	@Override
	public void touchStart(ImageEditorView context, float xF, float yF) {
		rectVertexSelected = true;
		if (pointsAreClose(xF, yF, selection.left, selection.top)) {
			fixedHorizontalEdge = selection.right;
			fixedVerticalEdge = selection.bottom;
			toRightOfFixed = false;
			toBottomOfFixed = false;
		} else if (pointsAreClose(xF, yF, selection.right, selection.top)) {
			fixedHorizontalEdge = selection.left;
			fixedVerticalEdge = selection.bottom;
			toRightOfFixed = true;
			toBottomOfFixed = false;
		} else if (pointsAreClose(xF, yF, selection.right, selection.bottom)) {
			fixedHorizontalEdge = selection.left;
			fixedVerticalEdge = selection.top;
			toRightOfFixed = true;
			toBottomOfFixed = true;
		} else if (pointsAreClose(xF, yF, selection.left, selection.bottom)) {
			fixedHorizontalEdge = selection.right;
			fixedVerticalEdge = selection.top;
			toRightOfFixed = false;
			toBottomOfFixed = true;
		} else {
			rectVertexSelected = false;
			int x = Math.round(xF);
			int y = Math.round(yF);
			if (selection.contains(x, y)) {
				xStart = x;
				yStart = y;
				initialSelection.set(selection);
				rectSelected = true;
			}
		}
	}
	
	private void offset(ImageEditorView context, int dx, int dy) {
		Rect imageVisibleRegionBounds = context.getImageVisibleRegionBounds();
		if (dx > 0) {
			dx = Math.min(dx, imageVisibleRegionBounds.right - selection.right);
		} else {
			dx = Math.max(dx, imageVisibleRegionBounds.left - selection.left);
		}
		if (dy > 0) {
			dy = Math.min(dy, imageVisibleRegionBounds.bottom - selection.bottom);
		} else {
			dy = Math.max(dy, imageVisibleRegionBounds.top - selection.top);
		}
		selection.offset(dx, dy);
	}

	@Override
	public void touchMove(ImageEditorView context, float xF, float yF) {
		Rect imageVisibleRegionBounds = context.getImageVisibleRegionBounds();
		int x = Math.round(xF);
		int y = Math.round(yF);
		if (rectVertexSelected) {
			if (toRightOfFixed) {
				x = Math.min(Math.max(x, fixedHorizontalEdge + MIN_SELECTION_RECT_WIDTH), imageVisibleRegionBounds.right);
			} else {
				x = Math.min(Math.max(x, imageVisibleRegionBounds.left), fixedHorizontalEdge - MIN_SELECTION_RECT_WIDTH);
			}
			if (toBottomOfFixed) {
				y = Math.min(Math.max(y, fixedVerticalEdge + MIN_SELECTION_RECT_HEIGHT), imageVisibleRegionBounds.bottom);
			} else {
				y = Math.min(Math.max(y, imageVisibleRegionBounds.top), fixedVerticalEdge - MIN_SELECTION_RECT_HEIGHT);
			}
			selection.set(fixedHorizontalEdge, fixedVerticalEdge, x, y);
			selection.sort();
			context.invalidate();
		} else if (rectSelected) {
			selection.set(initialSelection);
			offset(context, x - xStart, y - yStart);
			context.invalidate();
		}
	}

	@Override
	public void touchUp(ImageEditorView context) {
		rectVertexSelected = false;
		rectSelected = false;
	}
	
	public Rect clearSelection() {
		Rect selectionCopy = selection;
		selection = null;
		return selectionCopy;
	}
	
	@Override
	public void crop(ImageEditorView context) {
		if (selection != null) {
			RectF originalSelection = new RectF();
			context.getScreenToOriginal().mapRect(originalSelection, new RectF(clearSelection()));
			context.commandManager.executeCommand(context, context.commandFactory.createCropCommand(context.getOriginalBitmapWrapper()), originalSelection);
		}
	}
	
	public static boolean canCrop(ImageEditorView context) {
		Rect imageVisibleRegionBounds = context.getImageVisibleRegionBounds();
		int width = imageVisibleRegionBounds.width();
		int height = imageVisibleRegionBounds.height();
		int minWidth = MIN_SELECTION_RECT_WIDTH;
		int minHeight = MIN_SELECTION_RECT_HEIGHT;
		return  (width >= minWidth) && (height >= minHeight) && (height != minHeight || width != minWidth);
	}
	
}
