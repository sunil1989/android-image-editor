package com.android.image.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.android.image.edit.command.AbstractCommand;
import com.android.image.edit.command.AbstractMultiTargetCommand;
import com.android.image.edit.command.factory.CommandFactory;
import com.android.image.edit.command.factory.MementoUndoableCommandFactory;
import com.android.image.edit.command.factory.SimpleCommandFactory;
import com.android.image.edit.command.manager.CommandManager;
import com.android.image.edit.command.manager.CommandManagerImpl;
import com.android.image.edit.command.manager.UndoableCommandManager;
import com.android.image.edit.scroll.ImageScrollState;
import com.android.image.edit.scroll.ImageScrollStateFactory;
import com.android.image.edit.tool.EraseTool;
import com.android.image.edit.tool.Tool;
import com.android.image.edit.transform.ImageTransformStrategy;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ImageEditorView extends View {
	
	public static int BACKGROUND_COLOR = 0xFF424542;

	//private Bitmap  mBitmap;
	private FileBitmap originalCanvasBitmap = new FileBitmap(BACKGROUND_COLOR);
	private Bitmap transformedBitmap;
    //private Canvas  mCanvas = new Canvas();
    private Canvas transformedCanvas = new Canvas();
    private Paint   mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    public CommandManager<AbstractMultiTargetCommand<BitmapWrapper>> commandManager = new CommandManagerImpl(5);
    public CommandFactory<BitmapWrapper, AbstractMultiTargetCommand<BitmapWrapper>> commandFactory = SimpleCommandFactory.getInstance();
    private Tool currentTool = new EraseTool(this);
    private Matrix transform = new Matrix();
    private Matrix inverse = new Matrix();
    private ImageTransformStrategy imageTransformStrategy = ImageTransformStrategy.FIT_TO_SCREEN_SIZE;
    private ImageScrollState imageScrollState;
    
    public ImageEditorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
    
    private void clearCanvas(Canvas canvas) {
    	canvas.drawColor(BACKGROUND_COLOR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	clearCanvas(canvas);

    	drawTransformedBitmap(canvas);
    	
        currentTool.onDraw(this, canvas);
    }

	public void setScrollByX(float scrollByX) {
		imageScrollState.setScrollByX(scrollByX);
	}

	public void setScrollByY(float scrollByY) {
		imageScrollState.setScrollByY(scrollByY);
	}

	public FileBitmap getOriginalCanvasBitmap() {
		return originalCanvasBitmap;
	}

	/*public CommandManager<AbstractMultiTargetCommand<CanvasBitmap>> getCommandManager() {
		return commandManager;
	}
	
	public CommandFactory<CanvasBitmap, AbstractMultiTargetCommand<CanvasBitmap>> getCommandFactory() {
		return commandFactory;
	}*/

	private void drawTransformedBitmap(Canvas canvas) {
    	if (transformedBitmap != null) {
    		imageScrollState.drawBitmap(canvas, transformedBitmap, mBitmapPaint, getWidth(), getHeight());
    	}
    }
	
	public void updateTransformedBitmap(boolean resetTransformAndScrollState) {
		Bitmap originalBitmap = originalCanvasBitmap.getPlainBitmap();
		updateTransformedBitmap(originalBitmap, resetTransformAndScrollState);
		originalBitmap.recycle();
	}
	
	public void updateTransformedBitmap(Bitmap originalBitmap, boolean resetTransformAndScrollState) {
		if (resetTransformAndScrollState) {
			boolean imageFitToView = imageTransformStrategy.prepareTransformAndCheckFit(transform, originalBitmap.getWidth(), originalBitmap.getHeight(), getWidth(), getHeight());
			transform.invert(inverse);
			imageScrollState = ImageScrollStateFactory.getInstance().createImageScrollState(imageFitToView);
		}
		if (transformedBitmap != null) {
			transformedBitmap.recycle();
		}
		transformedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), transform, false);
		transformedCanvas.setBitmap(transformedBitmap);
	}
	
	public void changeImageTransformStrategy(ImageTransformStrategy imageTransformStrategy) {
		this.imageTransformStrategy = imageTransformStrategy;
		updateTransformedBitmap(true);
		invalidate();
	}

	@Override
    public boolean onTouchEvent(MotionEvent event) {
		if (transformedBitmap == null) {
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
	
    public Canvas getCanvas() {
		return originalCanvasBitmap.getCanvas();
	}

	public Canvas getTransformedCanvas() {
		return transformedCanvas;
	}

	public Tool getCurrentTool() {
		return currentTool;
	}
	
	public void setOriginalAndUpdateTransformedBitmap(String bitmapFilePath) {
		setOriginalBitmap(bitmapFilePath);
		updateTransformedBitmap(true);
	}
	
	public void setOriginalBitmap(String bitmapFilePath) {
		/*if (mBitmap != null && bitmap.getWidth() == mBitmap.getWidth() && bitmap.getHeight() == mBitmap.getHeight()) {
			clearCanvas(mCanvas);
			mCanvas.drawBitmap(bitmap, 0, 0, null);
			updateTransformedBitmap(false);
			return;
		}
		if (mBitmap != null) {
			mBitmap.recycle();
		}
		mBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);
		mCanvas.drawBitmap(bitmap, 0, 0, null);*/
		//originalCanvasBitmap = new FileBitmap(BACKGROUND_COLOR, bitmapFilePath);
		originalCanvasBitmap.setBitmap(bitmapFilePath);
	}
	
	public float getOriginalRadius(float transformedRadius) {
		return inverse.mapRadius(transformedRadius);
	}
	
	public Matrix getInverseTransform() {
		Matrix matrix = new Matrix();
		matrix.set(imageScrollState.getTranslate());
		matrix.postConcat(inverse);
		return matrix;
	}
	
	public void setBitmap(String bitmapFilePath) {
		setOriginalAndUpdateTransformedBitmap(bitmapFilePath);
		invalidate();
	}
	
	public void undo() {
		if (commandManager.hasMoreUndo()) {
			commandManager.undo();
			Bitmap initialBitmap = originalCanvasBitmap.getBitmap();
			Bitmap bitmap = commandManager.getCurrentOriginalBitmap(initialBitmap);
			updateTransformedBitmap(bitmap, true);
			bitmap.recycle();
			initialBitmap.recycle();
			invalidate();
		}
	}
	
	public void changeTool(Tool tool) {
		currentTool = tool;
	}
	
	public void crop() {
		currentTool.crop(this);
	}
    
    public Bitmap getBitmap() {
		return originalCanvasBitmap.getBitmap();
	}
	
	public Bitmap getTransformedBitmap() {
		return transformedBitmap;
	}
	
	public RectF getImageVisibleRegionBounds() {
		return imageScrollState.getVisibleRegionBounds(this);
	}
	
	public Matrix getTranslate() {
		return imageScrollState.getTranslate();
	}
	
	public Matrix getInverse() {
		return inverse;
	}
	
}
