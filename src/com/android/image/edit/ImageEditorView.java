package com.android.image.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.android.image.edit.command.CommandManager;
import com.android.image.edit.memento.Memento;
import com.android.image.edit.memento.MementoOriginator;
import com.android.image.edit.tool.EraseTool;
import com.android.image.edit.tool.Tool;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ImageEditorView extends View implements MementoOriginator<ImageEditorView.ImageEditorViewMemento> {

	private Bitmap  mBitmap;
    private Canvas  mCanvas;
    private Paint   mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private CommandManager commandManager = new CommandManager(2);
    private Tool currentTool = new EraseTool(this);
    private Matrix transform = new Matrix();
    private Matrix inverse = new Matrix();
    
    public ImageEditorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    @Override
    protected void onDraw(Canvas canvas) {
        currentTool.onDraw(this, canvas);
    }

    public CommandManager getCommandManager() {
		return commandManager;
	}
    
    public void drawTransformedBitmap(Canvas canvas) {
    	if (mBitmap != null) {
    		Bitmap transformedBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), transform, false);
    		canvas.drawBitmap(transformedBitmap, 0, 0, mBitmapPaint);
    		transformedBitmap.recycle();
    	}
    }

	@Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentTool.touchStart(this, x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                currentTool.touchMove(this, x, y);
                break;
            case MotionEvent.ACTION_UP:
                currentTool.touchUp(this);
                break;
        }
        return true;
    }
	
    public Canvas getCanvas() {
		return mCanvas;
	}

	public Tool getCurrentTool() {
		return currentTool;
	}

	public ImageEditorViewMemento createMemento() {
		return new ImageEditorViewMemento(mBitmap.copy(Bitmap.Config.ARGB_8888, false));
	}
	
	public void setMemento(ImageEditorViewMemento memento) {
		redrawBitmap(memento.state);
		invalidate();
	}
	
	public void redrawBitmap(Bitmap bitmap) {
		transform.reset();
		if (bitmap.getWidth() > bitmap.getHeight()) {
			transform.postRotate(90);
			transform.postTranslate(bitmap.getHeight(), 0);
		}
		float bitmapWidth = Math.min(bitmap.getWidth(), bitmap.getHeight());
		float bitmapHeight = Math.max(bitmap.getWidth(), bitmap.getHeight());
		float scale = Math.min(getWidth()/bitmapWidth, getHeight()/bitmapHeight);
		transform.postScale(scale, scale);
		transform.invert(inverse);
		if (mBitmap != null) {
			mBitmap.recycle();
		}
		mBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
		mCanvas.drawBitmap(bitmap, 0, 0, null);
	}
	
	public float[] getOriginalPoints(float... transformCoord) {
		float[] result = new float[transformCoord.length];
		inverse.mapPoints(result, transformCoord);
		return result;
	}
	
	public float[] getTransformedPoint(float originalX, float originalY) {
		float[] result = new float[2];
		transform.mapPoints(result, new float[]{originalX, originalY});
		return result;
	}
	
	public float getTransformedRadius(float originalRadius) {
		return transform.mapRadius(originalRadius);
	}
	
	public RectF getOriginalRect(RectF transformedRect) {
		RectF result = new RectF();
		inverse.mapRect(result, transformedRect);
		return result;
	}
	
	public void setBitmap(Bitmap bitmap) {
		redrawBitmap(bitmap);
		invalidate();
	}
	
	public void undo() {
		commandManager.undo();
	}
	
	public void changeTool(Tool tool) {
		currentTool = tool;
	}
	
	public void crop() {
		currentTool.crop(this);
	}
    
    public Bitmap getBitmap() {
		return mBitmap;
	}
	
	public void drawPath() {
		currentTool.drawPath(this);
	}

	public static class ImageEditorViewMemento implements Memento {
		
		private Bitmap state;

		private ImageEditorViewMemento(Bitmap state) {
			this.state = state;
		}
		
		public void recycle() {
			state.recycle();
		}
		
	}
	
}
