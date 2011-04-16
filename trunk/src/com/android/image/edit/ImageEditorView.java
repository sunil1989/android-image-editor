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
import com.android.image.edit.scroll.ImageScrollState;
import com.android.image.edit.scroll.ImageScrollStateFactory;
import com.android.image.edit.tool.EraseTool;
import com.android.image.edit.tool.Tool;
import com.android.image.edit.transform.ImageTransformStrategy;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ImageEditorView extends View implements MementoOriginator<ImageEditorView.ImageEditorViewMemento> {
	
	public static int BACKGROUND_COLOR = 0xFF424542;

	private Bitmap  mBitmap;
	private Bitmap transformedBitmap;
    private Canvas  mCanvas = new Canvas();
    private Canvas transformedCanvas = new Canvas();
    private Paint   mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private CommandManager commandManager = new CommandManager(2);
    private Tool currentTool = new EraseTool(this);
    private Matrix transform = new Matrix();
    private Matrix inverse = new Matrix();
    private ImageTransformStrategy imageTransformStrategy = ImageTransformStrategy.FIT_TO_SCREEN_SIZE;
    private ImageScrollState imageScrollState;
    
    public ImageEditorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    @Override
    protected void onDraw(Canvas canvas) {
    	clearCanvas(canvas);

    	drawTransformedBitmap(canvas);
    	
        currentTool.onDraw(this, canvas);
    }
    
    private void clearCanvas(Canvas canvas) {
    	canvas.drawColor(BACKGROUND_COLOR);
    }

	public void setScrollByX(float scrollByX) {
		imageScrollState.setScrollByX(scrollByX);
	}

	public void setScrollByY(float scrollByY) {
		imageScrollState.setScrollByY(scrollByY);
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	private void drawTransformedBitmap(Canvas canvas) {
    	if (mBitmap != null) {
    		imageScrollState.drawBitmap(canvas, transformedBitmap, mBitmapPaint, getWidth(), getHeight());
    	}
    }
	
	private void resetTransformAndScrollState() {
		boolean imageFitToView = imageTransformStrategy.prepareTransformAndCheckFit(transform, mBitmap.getWidth(), mBitmap.getHeight(), getWidth(), getHeight());
		transform.invert(inverse);
		imageScrollState = ImageScrollStateFactory.getInstance().createImageScrollState(imageFitToView);
		updateTransformedBitmap();
	}
	
	public void updateTransformedBitmap() {
		if (transformedBitmap != null) {
			transformedBitmap.recycle();
		}
		transformedBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), transform, false);
		transformedCanvas.setBitmap(transformedBitmap);
	}
	
	public void changeImageTransformStrategy(ImageTransformStrategy imageTransformStrategy) {
		this.imageTransformStrategy = imageTransformStrategy;
		resetTransformAndScrollState();
		invalidate();
	}

	@Override
    public boolean onTouchEvent(MotionEvent event) {
		if (mBitmap == null) {
			return true;
		}
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentTool.touchStart(this, event);
                break;
            case MotionEvent.ACTION_MOVE:
            	currentTool.touchMove(this, event);
                break;
            case MotionEvent.ACTION_UP:
            	currentTool.touchUp(this);
                break;
        }
        return true;
    }
	
	public boolean superOnTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
	
    public Canvas getCanvas() {
		return mCanvas;
	}

	public Canvas getTransformedCanvas() {
		return transformedCanvas;
	}

	public Tool getCurrentTool() {
		return currentTool;
	}

	public ImageEditorViewMemento createMemento() {
		return new ImageEditorViewMemento(mBitmap.copy(Bitmap.Config.ARGB_8888, false));
	}
	
	public void setMemento(ImageEditorViewMemento memento) {
		drawBitmap(memento.state);
		invalidate();
	}
	
	public void drawBitmap(Bitmap bitmap) {
		if (mBitmap != null) {
			mBitmap.recycle();
		}
		mBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);
		mCanvas.drawBitmap(bitmap, 0, 0, null);
		resetTransformAndScrollState();
	}
	
	public float[] getOriginalPoints(float... transformedCoord) {
		imageScrollState.toAbsoluteCoordinates(transformedCoord);
		float[] result = new float[transformedCoord.length];
		inverse.mapPoints(result, transformedCoord);
		return result;
	}
	
	public float[] getTransformedPoints(float... originalCoord) {
		float[] result = new float[originalCoord.length];
		transform.mapPoints(result, originalCoord);
		imageScrollState.toRelativeCoordinates(result);
		return result;
	}
	
	public float getOriginalRadius(float transformedRadius) {
		return inverse.mapRadius(transformedRadius);
	}
	
	public RectF getOriginalRect(RectF transformedRect) {
		RectF result = new RectF();
		inverse.mapRect(result, transformedRect);
		return result;
	}
	
	public void setBitmap(Bitmap bitmap) {
		drawBitmap(bitmap);
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
	
	public Bitmap getTransformedBitmap() {
		return transformedBitmap;
	}

	public void drawPath() {
		currentTool.drawPath(this);
	}
	
	public RectF getImageVisibleRegionBounds() {
		return imageScrollState.getVisibleRegionBounds(this);
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
