package com.android.imgedit.tool;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import com.android.imgedit.ImageEditorView;
import com.android.imgedit.command.DrawPathCommand;

public class EraseTool implements Tool {
	
	private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private static final float DEFAULT_ORIGINAL_STROKE_WIDTH = 12;
    private Path scaledPath = new Path();
    private Path originalPath = new Path();
    private Paint originalPaint;
    private Paint scaledPaint;

	public EraseTool(ImageEditorView context) {
		originalPaint = createPaint(DEFAULT_ORIGINAL_STROKE_WIDTH);
		scaledPaint = createPaint(context.scaled(DEFAULT_ORIGINAL_STROKE_WIDTH));
	}
	
	private Paint createPaint(float strokeWidth) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(0xFFFF0000);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(strokeWidth);
		paint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.CLEAR));
        return paint;
	}

	@Override
	public void touchStart(ImageEditorView context, float x, float y) {
		scaledPath.reset();
		originalPath.reset();
		scaledPath.moveTo(x, y);
		originalPath.moveTo(context.orig(x), context.orig(y));
        mX = x;
        mY = y;
        context.invalidate();
	}

	@Override
	public void touchMove(ImageEditorView context, float x, float y) {
		float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
        	scaledPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
        	originalPath.quadTo(context.orig(mX), context.orig(mY), context.orig((x + mX)/2), context.orig((y + mY)/2));
            mX = x;
            mY = y;
        }
        context.invalidate();
	}

	@Override
	public void touchUp(ImageEditorView context) {
		scaledPath.lineTo(mX, mY);
		originalPath.lineTo(context.orig(mX), context.orig(mY));
        // commit the path to our offscreen
		context.getCommandManager().executeCommand(new DrawPathCommand(context));
        // kill this so we don't double draw
		scaledPath.reset();
		originalPath.reset();
        context.invalidate();
	}

	@Override
	public void onDraw(ImageEditorView context, Canvas canvas) {
		canvas.drawColor(0xFFAAAAAA);

        context.drawBitmap(canvas);

        canvas.drawPath(scaledPath, scaledPaint);
	}
	
	@Override
	public void drawPath(ImageEditorView context) {
    	context.getCanvas().drawPath(originalPath, originalPaint);
    }

	@Override
	public void crop(ImageEditorView context) {}
	
}
