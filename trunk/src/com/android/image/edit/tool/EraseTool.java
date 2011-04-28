package com.android.image.edit.tool;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import com.android.image.edit.ImageEditorView;

public class EraseTool implements Tool {
	
	private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private static final float DEFAULT_TRANSFORMED_STROKE_WIDTH = 15;
    private Path screenPath = new Path();
    private Paint paint;

	public EraseTool(ImageEditorView context) {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(ImageEditorView.BACKGROUND_COLOR);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(DEFAULT_TRANSFORMED_STROKE_WIDTH);
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
        if (Math.hypot(dx, dy) >= TOUCH_TOLERANCE) {
        	screenPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
            context.invalidate();
        }
	}
	
	@Override
	public void touchUp(ImageEditorView context) {
		screenPath.lineTo(mX, mY);
        // commit the path to our offscreen
		Path transformedPath = new Path();
    	screenPath.transform(context.getTranslate(), transformedPath);
    	context.getTransformedCanvas().drawPath(transformedPath, paint);
		Path originalPath = new Path();
		transformedPath.transform(context.getInverse(), originalPath);
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
        	canvas.drawPath(screenPath, paint);
        }
	}

	@Override
	public void crop(ImageEditorView context) {}
	
}
