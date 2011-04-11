package com.android.imgedit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import com.android.imgedit.command.CommandManager;
import com.android.imgedit.memento.Memento;
import com.android.imgedit.memento.MementoOriginator;
import com.android.imgedit.tool.EraseTool;
import com.android.imgedit.tool.Tool;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ImageEditorView extends View implements MementoOriginator<ImageEditorView.ImageEditorViewMemento> {

	private Bitmap  mBitmap;
    private Canvas  mCanvas;
    private Paint   mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private CommandManager commandManager = new CommandManager(2);
    private Tool currentTool = new EraseTool(this);
    private float scale;
    
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
    
    public void drawBitmap(Canvas canvas) {
    	if (mBitmap != null) {
    		Matrix matrix = new Matrix();
    		matrix.postScale(1/scale, 1/scale);
    		Bitmap resizedBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
    		canvas.drawBitmap(resizedBitmap, 0, 0, mBitmapPaint);
    		resizedBitmap.recycle();
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
		Bitmap newBitmap;
		if (bitmap.getWidth() > bitmap.getHeight()) {
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		} else {
			newBitmap = bitmap;
		}
		scale = Math.max(((float)newBitmap.getWidth())/getWidth(), ((float)newBitmap.getHeight())/getHeight());
		if (mBitmap != null) {
			mBitmap.recycle();
		}
		mBitmap = Bitmap.createBitmap(newBitmap.getWidth(), newBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
		mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		mCanvas.drawBitmap(newBitmap, 0, 0, null);
		newBitmap.recycle();
	}
	
	public float orig(float scaledCoord) {
		return scaledCoord*scale;
	}
	
	public float scaled(float originalCoord) {
		return originalCoord/scale;
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
