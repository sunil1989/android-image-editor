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
    private Path scaledPath = new Path();
    private Path originalPath = new Path();
    private Paint       mPaint;

	public EraseTool() {
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        mPaint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.CLEAR));
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

        canvas.drawPath(scaledPath, mPaint);
	}
	
	@Override
	public void drawPath(ImageEditorView context) {
    	context.getCanvas().drawPath(originalPath, mPaint);
    }

	@Override
	public void crop(ImageEditorView context) {}
	
}
