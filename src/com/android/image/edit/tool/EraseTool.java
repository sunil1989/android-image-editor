package com.android.image.edit.tool;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;

import com.android.image.edit.ImageEditorView;

public class EraseTool implements Tool {
	
	private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private static final float DEFAULT_TRANSFORMED_STROKE_WIDTH = 15;
    private Path screenPath = new Path();
    private Paint transformedPaint;
    private Paint transformedTempPaint;
    private Point topLeftCorner;

	public EraseTool(ImageEditorView context) {
		transformedPaint = createPaint(0xFFFF0000, true, DEFAULT_TRANSFORMED_STROKE_WIDTH);
		transformedTempPaint = createPaint(ImageEditorView.BACKGROUND_COLOR, false, DEFAULT_TRANSFORMED_STROKE_WIDTH);
	}
	
	private Paint createPaint(int color, boolean clear) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		if (clear) {
			paint.setXfermode(new PorterDuffXfermode(
					PorterDuff.Mode.CLEAR));
		}
        return paint;
	}
	
	private Paint createPaint(int color, boolean clear, float strokeWidth) {
		Paint paint = createPaint(color, clear);
		paint.setStrokeWidth(strokeWidth);
		return paint;
	}

	@Override
	public void touchStart(ImageEditorView context, MotionEvent event) {
		float x = event.getX(),
		y = event.getY();
		screenPath.reset();
		screenPath.moveTo(x, y);
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
        	screenPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
        context.invalidate();
	}
	
	@Override
	public void touchUp(ImageEditorView context) {
		screenPath.lineTo(mX, mY);
        // commit the path to our offscreen
		Path transformedPath = new Path();
    	screenPath.transform(context.getTranslate(), transformedPath);
    	context.getTransformedCanvas().drawPath(transformedPath, transformedPaint);
		Path originalPath = new Path();
		transformedPath.transform(context.getInverse(), originalPath);
		//screenPath.transform(context.getInverse(), originalPath);
		float strokeWidth = context.getOriginalRadius(DEFAULT_TRANSFORMED_STROKE_WIDTH);
		context.commandManager.executeCommand(context.commandFactory.createDrawPathCommand(context.getOriginalBitmapWrapper()), originalPath, strokeWidth);
        // kill this so we don't double draw
		screenPath.reset();
		transformedPath.reset();
        context.invalidate();
	}

	@Override
	public void onDraw(ImageEditorView context, Canvas canvas) {
        if (!screenPath.isEmpty()) {
        	canvas.drawPath(screenPath, transformedTempPaint);
        }
	}

	@Override
	public void crop(ImageEditorView context) {}
	
}
