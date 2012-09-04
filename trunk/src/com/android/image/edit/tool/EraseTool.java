package com.android.image.edit.tool;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.android.image.edit.ImageEditorView;
import com.android.image.edit.command.DefaultTargetImageEditCommand;

public class EraseTool implements Tool {
	
	private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private static final float DEFAULT_DISPLAYABLE_STROKE_WIDTH = 15;
    private Path screenPath = new Path();
    private Path transformedPath = new Path();
    private Paint paint;

	public EraseTool() {
		paint = new Paint();
		//paint.setAntiAlias(true);
		//paint.setDither(true);
		paint.setColor(ImageEditorView.BACKGROUND_COLOR);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(DEFAULT_DISPLAYABLE_STROKE_WIDTH);
	}

	@Override
	public void touchStart(ImageEditorView context, float x, float y) {
		screenPath.reset();
		screenPath.moveTo(x, y);
        mX = x;
        mY = y;
        context.invalidate();
	}

	@Override
	public void touchMove(ImageEditorView context, float x, float y) {
		float dx = Math.abs(x - mX),
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
    	screenPath.transform(context.getScreenToMaxZoom(), transformedPath);
    	DefaultTargetImageEditCommand eraseCommand = context.commandFactory.createEraseCommand(context.getOriginalBitmapWrapper());
    	float strokeWidth = context.getScreenToMaxZoom().mapRadius(DEFAULT_DISPLAYABLE_STROKE_WIDTH);
    	eraseCommand.execute(context.getMaxZoomDisplayedBitmapWrapper(), transformedPath, strokeWidth);
		Path originalPath = new Path();
		screenPath.transform(context.getScreenToOriginal(), originalPath);
		screenPath.reset();
        context.invalidate();
		strokeWidth = context.getScreenToOriginal().mapRadius(DEFAULT_DISPLAYABLE_STROKE_WIDTH);
		context.commandManager.executeCommand(context, eraseCommand, originalPath, strokeWidth);
	}

	@Override
	public void onDraw(ImageEditorView context, Canvas canvas) {
        if (!screenPath.isEmpty()) {
        	canvas.drawPath(screenPath, paint);
        }
	}

}
