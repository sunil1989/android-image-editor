package com.android.image.edit.tool;

import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import com.android.image.edit.ImageEditorView;
import com.android.image.edit.R;
import com.android.image.edit.command.DefaultTargetImageEditCommand;
import com.android.image.edit.command.manager.DefaultTargetImageEditCommandManager;

public class MagicWandTool implements Tool {
	
	private Point scrStartPoint = new Point();
	private Point origStartPoint = new Point();
	private Bitmap crosshairBitmap;
	private LinkedList<Point> scrStartPoints = new LinkedList<Point>();
	private byte eraseTolerance = 0;
	private boolean movingCrosshair = false;
	
	public MagicWandTool(ImageEditorView context) {
		crosshairBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.crosshair);
		initScrStartPoint(context);
		context.invalidate();
	}
	
	private void initScrStartPoint(ImageEditorView context) {
		Rect imageVisibleRegionBounds = context.getImageVisibleRegionBounds();
		scrStartPoint.set(imageVisibleRegionBounds.left + crosshairBitmap.getWidth()/2,
				imageVisibleRegionBounds.top + crosshairBitmap.getHeight()/2);
		updateOriginalPoint(context);
	}
	
	private void updateOriginalPoint(ImageEditorView context) {
		float[] originalPoint = new float[2];
		context.getScreenToOriginal().mapPoints(originalPoint, new float[]{scrStartPoint.x, scrStartPoint.y});
		origStartPoint.set(Math.round(originalPoint[0]), Math.round(originalPoint[1]));
	}

	@Override
	public void onDraw(ImageEditorView context, Canvas canvas) {
		canvas.drawBitmap(crosshairBitmap, scrStartPoint.x-crosshairBitmap.getWidth()/2.f, 
				scrStartPoint.y-crosshairBitmap.getHeight()/2.f, null);
	}

	@Override
	public void touchStart(ImageEditorView context, float xScrF, float yScrF) {
		if (!context.commandManager.isTaskRunning()) {
			int xScr = Math.round(xScrF);
			int yScr = Math.round(yScrF);
			if (context.getImageVisibleRegionBounds().contains(xScr, yScr)) {
				movingCrosshair = true;
				if (eraseTolerance != 0) {
					scrStartPoints.addLast(clonePoint(scrStartPoint));
				}
				scrStartPoint.set(xScr, yScr);
				context.invalidate();
			}
		}
	}
	
	@Override
	public void touchMove(ImageEditorView context, float xScrF, float yScrF) {
		if (movingCrosshair && !context.commandManager.isTaskRunning()) {
			Rect visibleRegionBounds = context.getImageVisibleRegionBounds();
			int xScr = Math.max(visibleRegionBounds.left, Math.min(Math.round(xScrF), visibleRegionBounds.right-1));
			int yScr = Math.max(visibleRegionBounds.top, Math.min(Math.round(yScrF), visibleRegionBounds.bottom-1));		
			scrStartPoint.set(xScr, yScr);
			context.invalidate();
		}
	}

	@Override
	public void touchUp(ImageEditorView context) {
		if (movingCrosshair && !context.commandManager.isTaskRunning()) {
			movingCrosshair = false;
			updateOriginalPoint(context);
			if (eraseTolerance != 0) {
				DefaultTargetImageEditCommand floodFillCommand = context.commandFactory.createFloodFillCommand(context.getOriginalBitmapWrapper());
				context.commandManager.executeCommand(context, floodFillCommand, clonePoint(origStartPoint), eraseTolerance, context.getHandler());
			}
		}
	}

	public void setEraseTolerance(byte eraseTolerance, final ImageEditorView context) {
		if (!context.commandManager.isTaskRunning()) {
			if (eraseTolerance != 0) {
				if (this.eraseTolerance != 0) {
					context.commandManager.popCommand(context);
				}
				DefaultTargetImageEditCommand floodFillCommand = context.commandFactory.createFloodFillCommand(context.getOriginalBitmapWrapper());
				context.commandManager.executeCommand(context, floodFillCommand, clonePoint(origStartPoint), eraseTolerance, context.getHandler());
			} else if (this.eraseTolerance != 0) {
				context.commandManager.undo(context);
			}
			this.eraseTolerance = eraseTolerance;
		}
	}
	
	private static Point clonePoint(Point point) {
		return new Point(point.x, point.y);
	}
	
	public void undo(ImageEditorView context) {
		if (!context.commandManager.isTaskRunning()) {
			if (eraseTolerance != 0) {
				eraseTolerance = 0;
				context.onMagicWandToleranceChange((byte)0);
			} else {
				Object[] params = context.commandManager.getLastCommandParams();
				Point lastScrStartPoint = scrStartPoints.removeLast();
				scrStartPoint.set(lastScrStartPoint.x, lastScrStartPoint.y);
				Point lastOrigStartPoint = (Point)params[0];
				origStartPoint.set(lastOrigStartPoint.x, lastOrigStartPoint.y);
			}
			context.commandManager.undo(context);
		}
	}
	
	public boolean hasMoreUndo(DefaultTargetImageEditCommandManager commandManager) {
		return commandManager.hasMoreUndo() && (scrStartPoints.size() > 0 || eraseTolerance != 0);
	}
	
}
