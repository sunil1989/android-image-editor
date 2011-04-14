package com.android.image.edit.tool;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;

import com.android.image.edit.ImageEditorView;
import com.android.image.edit.command.DrawPathCommand;

public class EraseTool implements Tool {
	
	private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private static final float DEFAULT_ORIGINAL_STROKE_WIDTH = 12;
    private Path transformedPath = new Path();
    private Path originalPath = new Path();
    private Paint originalPaint;
    private Paint transformedPaint;

	public EraseTool(ImageEditorView context) {
		originalPaint = createPaint();
		originalPaint.setStrokeWidth(DEFAULT_ORIGINAL_STROKE_WIDTH);
		transformedPaint = createPaint();
	}
	
	private Paint createPaint() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(0xFFFF0000);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.CLEAR));
        return paint;
	}

	@Override
	public void touchStart(ImageEditorView context, MotionEvent event) {
		float x = event.getX(),
		y = event.getY();
		transformedPath.reset();
		originalPath.reset();
		transformedPath.moveTo(x, y);
		float[] orig = context.getOriginalPoints(x, y);
		originalPath.moveTo(orig[0], orig[1]);
        mX = x;
        mY = y;
        context.invalidate();
	}

	@Override
	public void touchMove(ImageEditorView context, MotionEvent event) {
		float x = event.getX(),
		y = event.getY(),
		dx = Math.abs(x - mX),
        dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
        	transformedPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
        	float[] orig = context.getOriginalPoints(mX, mY, (x + mX)/2, (y + mY)/2);
        	originalPath.quadTo(orig[0], orig[1], orig[2], orig[3]);
            mX = x;
            mY = y;
        }
        context.invalidate();
	}

	@Override
	public void touchUp(ImageEditorView context) {
		transformedPath.lineTo(mX, mY);
		float[] orig = context.getOriginalPoints(mX, mY);
		originalPath.lineTo(orig[0], orig[1]);
        // commit the path to our offscreen
		context.getCommandManager().executeCommand(new DrawPathCommand(context));
        // kill this so we don't double draw
		transformedPath.reset();
		originalPath.reset();
        context.invalidate();
	}

	@Override
	public void onDraw(ImageEditorView context, Canvas canvas) {
        transformedPaint.setStrokeWidth(context.getTransformedRadius(DEFAULT_ORIGINAL_STROKE_WIDTH));
        if (!transformedPath.isEmpty()) {
        	canvas.drawPath(transformedPath, transformedPaint);
        }
	}
	
	@Override
	public void drawPath(ImageEditorView context) {
		transformedPaint.setStrokeWidth(context.getTransformedRadius(DEFAULT_ORIGINAL_STROKE_WIDTH));
    	context.getCanvas().drawPath(originalPath, originalPaint);
    	context.getTransformedCanvas().drawPath(transformedPath, transformedPaint);
    }

	@Override
	public void crop(ImageEditorView context) {}
	
}
