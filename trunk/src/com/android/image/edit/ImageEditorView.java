package com.android.image.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region.Op;

import com.android.image.edit.command.AbstractMultiTargetCommand;
import com.android.image.edit.command.factory.CommandFactory;
import com.android.image.edit.command.factory.SimpleCommandFactory;
import com.android.image.edit.command.manager.CommandManager;
import com.android.image.edit.command.manager.CommandManagerImpl;
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
	public static Bitmap.Config nonAlphaBitmapConfig = Bitmap.Config.RGB_565;
	public static Bitmap.Config alphaBitmapConfig = Bitmap.Config.ARGB_8888;

	private BitmapWrapper originalBitmapWrapper = new MemoryBitmap(BACKGROUND_COLOR, true);
	private Bitmap transformedBitmap;
    private Canvas transformedCanvas = new Canvas();
    private Paint   mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    public CommandManager<AbstractMultiTargetCommand<BitmapWrapper>> commandManager = new CommandManagerImpl(2);
    public CommandFactory<BitmapWrapper, AbstractMultiTargetCommand<BitmapWrapper>> commandFactory = SimpleCommandFactory.getInstance();
    private Tool currentTool = new EraseTool(this);
    private Matrix transform = new Matrix();
    private Matrix inverse = new Matrix();
    private ImageTransformStrategy imageTransformStrategy = ImageTransformStrategy.FIT_TO_SCREEN_SIZE;
    private ImageScrollState imageScrollState;
    private int originalBitmapWidth, originalBitmapHeight;
    
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

	public BitmapWrapper getOriginalBitmapWrapper() {
		return originalBitmapWrapper;
	}

	private void drawTransformedBitmap(Canvas canvas) {
    	if (transformedBitmap != null) {
    		imageScrollState.drawBitmap(canvas, transformedBitmap, mBitmapPaint, getWidth(), getHeight());
    	}
    }
	
	public void updateTransformedBitmap(boolean imageTransformStrategyChanged) {
		Bitmap initialOriginalBitmap = originalBitmapWrapper.getBitmap();
		Bitmap originalBitmap = commandManager.applyPendingCommands(initialOriginalBitmap, false, originalBitmapWrapper.needMakeCopy());
		updateTransformedBitmap(originalBitmap, imageTransformStrategyChanged);
		originalBitmap.recycle();
		originalBitmapWrapper.recycle(initialOriginalBitmap);
	}
	
	public void updateTransformedBitmap(Bitmap originalBitmap, boolean imageTransformStrategyChanged) {
		if (imageTransformStrategyChanged || originalBitmap.getWidth() != originalBitmapWidth || originalBitmap.getHeight() != originalBitmapHeight) {
			boolean imageFitToView = imageTransformStrategy.prepareTransformAndCheckFit(transform, originalBitmap.getWidth(), originalBitmap.getHeight(), getWidth(), getHeight());
			transform.invert(inverse);
			imageScrollState = ImageScrollStateFactory.getInstance().createImageScrollState(imageFitToView);
			originalBitmapWidth = originalBitmap.getWidth();
			originalBitmapHeight = originalBitmap.getHeight();
		}
		if (transformedBitmap != null) {
			transformedBitmap.recycle();
		}
		Bitmap temp = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), transform, false);
		transformedBitmap = temp.copy(nonAlphaBitmapConfig, true);
		temp.recycle();
		transformedCanvas.setBitmap(transformedBitmap);
		transformedCanvas.clipRect(0, 0, transformedCanvas.getWidth(), transformedCanvas.getHeight(), Op.REPLACE);
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

	public Canvas getTransformedCanvas() {
		return transformedCanvas;
	}

	public Tool getCurrentTool() {
		return currentTool;
	}
	
	public void setOriginalBitmap(String bitmapFilePath) {
		originalBitmapWrapper.setBitmap(bitmapFilePath);
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
		setOriginalBitmap(bitmapFilePath);
		updateTransformedBitmap(true);
		invalidate();
	}
	
	public void undo() {
		if (commandManager.hasMoreUndo()) {
			commandManager.undo();
			updateTransformedBitmap(false);
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
		return commandManager.applyPendingCommands(originalBitmapWrapper.getBitmap(), true, originalBitmapWrapper.needMakeCopy());
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
	
	public Point getTopLeftCorner() {
		return imageScrollState.getTopLeftCorner();
	}
	
	public Matrix getInverse() {
		return inverse;
	}
	
}
