package android.image.editor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.image.editor.MainActivity.MyView;

public class EraseTool implements Tool {
	
	private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private Path mPath = new Path();
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
	public void touchStart(MyView context, float x, float y) {
		mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        context.invalidate();
	}

	@Override
	public void touchMove(MyView context, float x, float y) {
		float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
        context.invalidate();
	}

	@Override
	public void touchUp(MyView context) {
		mPath.lineTo(mX, mY);
        // commit the path to our offscreen
		context.getCommandManager().executeCommand(new DrawPathCommand(context));
        // kill this so we don't double draw
        mPath.reset();
        context.invalidate();
	}

	@Override
	public void onDraw(MyView context, Canvas canvas) {
		canvas.drawColor(0xFFAAAAAA);

        context.drawBitmap(canvas);

        canvas.drawPath(mPath, mPaint);
	}
	
	public void drawPath(MyView context) {
    	context.getCanvas().drawPath(mPath, mPaint);
    }
	
}
