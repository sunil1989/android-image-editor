package com.android.image.edit.tool.select;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.android.image.edit.ImageEditorView;

public class EditRectSelectionTool extends AbstractSelectionTool {

	private static float INIT_SELECTION_SIZE_PERCENT = 80;
	private static int SELECTION_RECT_PAINT_COLOR = 0xFF000000;
	private static int SELECTION_RECT_FILL_COLOR = 0x70FFFFFF;
	private static final float TOUCH_TOLERANCE = 30;
	private static final float MIN_SELECTION_RECT_WIDTH = 3*TOUCH_TOLERANCE;
	private static final float MIN_SELECTION_RECT_HEIGHT = 3*TOUCH_TOLERANCE;
	private float fixedVerticalEdge, fixedHorizontalEdge;
	private boolean rectVertexSelected = false;
	private boolean rectSelected = false;
	private float xStart, yStart;
	private RectF imageVisibleRegionBounds;
	private RectF initialSelection = new RectF();
	private boolean toRightOfFixed, toBottomOfFixed;
	private Paint vertexesPaint = new Paint();

	public EditRectSelectionTool(ImageEditorView context) {
		super(SELECTION_RECT_PAINT_COLOR, SELECTION_RECT_FILL_COLOR);
		imageVisibleRegionBounds = context.getImageVisibleRegionBounds();
		selection.set(imageVisibleRegionBounds);
		float indentPart = (100 - INIT_SELECTION_SIZE_PERCENT)/200;
		selection.inset(indentPart*imageVisibleRegionBounds.width(), indentPart*imageVisibleRegionBounds.height());
		context.invalidate();
	}
	
	private boolean pointsAreClose(float x1, float y1, float x2, float y2) {
		return Math.hypot(x1-x2, y1-y2) < TOUCH_TOLERANCE;
	}
	
	@Override
	public void touchStart(ImageEditorView context, MotionEvent event) {
		float x = event.getX(),
		y = event.getY();
		rectVertexSelected = true;
		if (pointsAreClose(x, y, selection.left, selection.top)) {
			fixedHorizontalEdge = selection.right;
			fixedVerticalEdge = selection.bottom;
			toRightOfFixed = false;
			toBottomOfFixed = false;
		} else if (pointsAreClose(x, y, selection.right, selection.top)) {
			fixedHorizontalEdge = selection.left;
			fixedVerticalEdge = selection.bottom;
			toRightOfFixed = true;
			toBottomOfFixed = false;
		} else if (pointsAreClose(x, y, selection.right, selection.bottom)) {
			fixedHorizontalEdge = selection.left;
			fixedVerticalEdge = selection.top;
			toRightOfFixed = true;
			toBottomOfFixed = true;
		} else if (pointsAreClose(x, y, selection.left, selection.bottom)) {
			fixedHorizontalEdge = selection.right;
			fixedVerticalEdge = selection.top;
			toRightOfFixed = false;
			toBottomOfFixed = true;
		} else {
			rectVertexSelected = false;
			if (selection.contains(x, y)) {
				xStart = x;
				yStart = y;
				initialSelection.set(selection);
				rectSelected = true;
			}
		}
	}
	
	private void offset(float dx, float dy) {
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
	public void touchMove(ImageEditorView context, MotionEvent event) {
		float x = event.getX(),
		y = event.getY();
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
			offset(x - xStart, y - yStart);
			context.invalidate();
		}
	}

	@Override
	public void touchUp(ImageEditorView context) {
		rectVertexSelected = false;
		rectSelected = false;
	}

	@Override
	public void onDraw(ImageEditorView context, Canvas canvas) {
		super.onDraw(context, canvas);
		canvas.drawRect(new Rect(), vertexesPaint);
	}
	
}
