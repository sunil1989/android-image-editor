package com.android.image.edit;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import com.android.image.edit.command.factory.BaseImageEditCommandFactory;
import com.android.image.edit.command.factory.DefaultTargetImageEditCommandFactory;
import com.android.image.edit.command.factory.ParamsValidatingImageEditCommandFactory;
import com.android.image.edit.command.manager.DefaultTargetImageEditCommandManager;
import com.android.image.edit.scale.ImageSizeState;
import com.android.image.edit.scale.ImageSizeStateFactory;
import com.android.image.edit.scroll.ImageScrollState;
import com.android.image.edit.scroll.ImageScrollStateFactory;
import com.android.image.edit.tool.MagicWandTool;
import com.android.image.edit.tool.ScrollTool;
import com.android.image.edit.tool.Tool;
import com.android.image.edit.tool.select.SelectionTool;
import com.android.image.edit.tool.select.SelectionToolImpl;
import com.android.image.edit.util.MemoryBitmapSource;
import com.android.image.edit.util.Source;

public class ImageEditorView extends View {
	
	public static final int BACKGROUND_COLOR = 0xFFFFFFFF;
	public static final Bitmap.Config DISPLAYABLE_BITMAP_CONFIG = Bitmap.Config.RGB_565;
	public static final Bitmap.Config ORIGINAL_BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

	private BitmapWrapper originalBitmapWrapper = new MemoryBitmap(this, true);
	private BitmapWrapper maxZoomDisplayableBitmapWrapper = new MemoryBitmap(this, false);
    public DefaultTargetImageEditCommandManager commandManager = new DefaultTargetImageEditCommandManager(7);
    public DefaultTargetImageEditCommandFactory commandFactory = new DefaultTargetImageEditCommandFactory(
    		new ParamsValidatingImageEditCommandFactory(new BaseImageEditCommandFactory()));
    private Tool currentTool = new ScrollTool();
    private Matrix maxZoomToCurrentZoom = new Matrix();
    private Matrix screenToMaxZoom = new Matrix();
    private Matrix originalToMaxZoom = new Matrix();
    private Matrix screenToOriginal = new Matrix();
    private ImageScrollState scrollState;
    private int originalBitmapWidth, originalBitmapHeight;
    private ImageSizeState sizeState;
    private List<ImageSizeChangeListener> imageSizeChangeListeners;
    private List<MagicWandToleranceChangeListener> magicWandToleranceChangeListeners;
    private float maxZoomToCurrentZoomScale;
    public StringBuilder log = new StringBuilder();
    
    public ImageEditorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
    
    private void clearCanvas(Canvas canvas) {
    	canvas.drawColor(BACKGROUND_COLOR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	clearCanvas(canvas);
    	drawDisplayableBitmap(canvas);
        currentTool.onDraw(this, canvas);
    }

	public void setScrollByXY(float scrollByX, float scrollByY) {
		scrollState.setScrollByXY(scrollByX, scrollByY);
	}

	public BitmapWrapper getOriginalBitmapWrapper() {
		return originalBitmapWrapper;
	}

	public BitmapWrapper getMaxZoomDisplayedBitmapWrapper() {
		return maxZoomDisplayableBitmapWrapper;
    	}

	private void drawDisplayableBitmap(Canvas canvas) {
    	if (maxZoomDisplayableBitmapWrapper.getBitmap() != null) {
    		try {
    			scrollState.drawBitmap(canvas, maxZoomDisplayableBitmapWrapper.getBitmap(), maxZoomToCurrentZoom);
    		} catch (IllegalArgumentException e) {
    			throw new IllegalArgumentException(new StringBuilder(log.toString())
    				.append(e.getMessage())
    				.append("\nZoom state is ").append(getCurrentZoomState())
    				.append(".\nImage size state is ").append(sizeState.getClass().getSimpleName())
    				.append(".\nOriginal image dimensions are: width=").append(originalBitmapWidth).append(", height=").append(originalBitmapHeight)
    				.append(".\nView dimensions are: width=").append(getWidth()).append(", height=").append(getHeight()).append(".")
    				.toString());
    		}
    	}
    }
	
	public void updateDisplayableBitmap() {
		updateDisplayableBitmap(true);
    }
	
	public boolean updateDisplayableBitmap(boolean updateUI) {
		Bitmap initialOriginalBitmap = originalBitmapWrapper.getBitmap();
		Bitmap originalBitmap = commandManager.applyPendingCommands(this, initialOriginalBitmap, false);
		boolean imageSizeChanged = updateDisplayableBitmap(originalBitmap, updateUI);
		originalBitmap.recycle();
		return imageSizeChanged;
	}
	
	public void onMagicWandToleranceChange(byte newTolerance) {
		if (magicWandToleranceChangeListeners != null) {
			for (MagicWandToleranceChangeListener listener : magicWandToleranceChangeListeners) {
				listener.onMagicWandToleranceChange(newTolerance);
			}
		}
	}
	
	public boolean updateDisplayableBitmap(Bitmap originalBitmap, boolean updateUI) {
		boolean imageSizeChanged = originalBitmap.getWidth() != originalBitmapWidth || originalBitmap.getHeight() != originalBitmapHeight;
		if (imageSizeChanged) {
			sizeState = ImageSizeStateFactory.getInstance().createImageSizeState(originalBitmap.getWidth(), originalBitmap.getHeight(), getWidth(), getHeight());
			if (updateUI) {
				updateUIAfterImageSizeChange();
			}
			originalBitmapWidth = originalBitmap.getWidth();
			originalBitmapHeight = originalBitmap.getHeight();
			float originalToMaxZoomScale = sizeState.getOriginalToMaxZoomScale();
			originalToMaxZoom.setScale(originalToMaxZoomScale, originalToMaxZoomScale);
			updateScrollStateAndMatrices();
		}
		maxZoomDisplayableBitmapWrapper.setBitmap(originalBitmap, originalToMaxZoom);
		return imageSizeChanged;
	}
	
	public void updateScreenToMatrices() {
		screenToMaxZoom.set(scrollState.getScreenToCurrentZoom());
		screenToMaxZoom.postScale(1/maxZoomToCurrentZoomScale, 1/maxZoomToCurrentZoomScale);
		screenToOriginal.set(scrollState.getScreenToCurrentZoom());
		float currentZoomToOriginalScale = sizeState.getCurrentZoomToOriginalScale();
		screenToOriginal.postScale(currentZoomToOriginalScale, currentZoomToOriginalScale);
	}
	
	private void updateScrollStateAndMatrices() {
		scrollState = ImageScrollStateFactory.getInstance().createImageScrollState(sizeState.getImageWidth(), sizeState.getImageHeight(), getWidth(), getHeight(), this);
		maxZoomToCurrentZoomScale = sizeState.getMaxZoomToCurrentZoomScale();
		maxZoomToCurrentZoom.setScale(maxZoomToCurrentZoomScale, maxZoomToCurrentZoomScale);
		updateScreenToMatrices();
	}
	
	public void updateUIAfterImageSizeChange() {
		if (imageSizeChangeListeners != null) {
			for (ImageSizeChangeListener listener : imageSizeChangeListeners) {
				listener.onImageSizeChange(sizeState.getCurrentZoomState());
			}
		}
	}

	@Override
    public boolean onTouchEvent(MotionEvent event) {
		return onTouchEvent(event.getX(), event.getY(), event.getAction());
    }
	
	public boolean onTouchEvent(float x, float y, int eventAction) {
		if (maxZoomDisplayableBitmapWrapper.getBitmap() == null) {
			return true;
		}
        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                onTouchStart(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
            	onTouchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
            	onTouchUp();
                break;
        }
        return true;
    }

	public void onTouchStart(float x, float y) {
		currentTool.touchStart(this, x, y);
	}

	public void onTouchMove(float x, float y) {
		currentTool.touchMove(this, x, y);
	}
	
	public void onTouchUp() {
		currentTool.touchUp(this);
	}
	
	public Tool getCurrentTool() {
		return currentTool;
	}
	
	public void setBitmap(Bitmap bitmap) {
		if (getContext() instanceof Activity) {
			DisplayMetrics metrics = new DisplayMetrics();
			((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
			log.append("Display width: ").append(metrics.widthPixels).append(", display height: ").append(metrics.heightPixels).append(", density: ").append(metrics.density).append("\n");
		}
		originalBitmapWrapper.setBitmap(bitmap, null);
		if (getWidth() > 0 && getHeight() > 0) {
			updateDisplayableBitmap();
		invalidate();
	}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (originalBitmapWrapper.getBitmap() != null) {
			updateDisplayableBitmap();
		invalidate();
	}
	}
	
	public void undo() {
		if (commandManager.hasMoreUndo()) {
			commandManager.undo(this);
		}
	}
	
	public void changeTool(Tool tool) {
		currentTool = tool;
	}
	
	public boolean canCrop() {
		return SelectionToolImpl.canCrop(this);
	}
    
	public void crop() {
		if (currentTool instanceof SelectionTool) {
			((SelectionTool)currentTool).crop(this);
	}
	}
	
    public Source<Bitmap> getBitmap() {
    	commandManager.applyPendingCommandsInPlace();
    	commandManager.setImageChanged(false);
		return new MemoryBitmapSource(originalBitmapWrapper.getBitmap());
	}
	
    public Source<Bitmap> getBitmapCopy() {
		return new MemoryBitmapSource(commandManager.applyPendingCommands(this, originalBitmapWrapper.getBitmap(), true));
	}
	
	public Rect getImageVisibleRegionBounds() {
		return scrollState.getVisibleRegionBounds();
	}
	
	public void performZoomAction() {
		sizeState.performZoomAction();
		updateScrollStateAndMatrices();
		invalidate();
	}
	
	public ImageSizeState.ZoomState getCurrentZoomState() {
		return sizeState == null ? null : sizeState.getCurrentZoomState();
	}

	public void setMagicWandEraseTolerance(byte magicWandTolerance) {
		if (currentTool instanceof MagicWandTool) {
			((MagicWandTool)currentTool).setEraseTolerance(magicWandTolerance, this);
		}
	}
	
	public boolean isImageChanged() {
		return commandManager.isImageChanged();
	}
	
	public void setImageChanged(boolean imageChanged) {
		commandManager.setImageChanged(imageChanged);
	}
	
	public Matrix getScreenToMaxZoom() {
		return screenToMaxZoom;
	}

	public Matrix getScreenToOriginal() {
		return screenToOriginal;
	}
	
	public ImageScrollState getScrollState() {
		return scrollState;
	}

	public void addImageSizeChangeListener(ImageSizeChangeListener imageSizeChangeListener) {
		if (imageSizeChangeListeners == null) {
			imageSizeChangeListeners = new ArrayList<ImageSizeChangeListener>();
		}
		imageSizeChangeListeners.add(imageSizeChangeListener);
	}
	
	public void addMagicWandToleranceChangeListener(MagicWandToleranceChangeListener listener) {
		if (magicWandToleranceChangeListeners == null) {
			magicWandToleranceChangeListeners = new ArrayList<MagicWandToleranceChangeListener>();
		}
		magicWandToleranceChangeListeners.add(listener);
	}
	
	public void removeAllListeners() {
		if (imageSizeChangeListeners != null) {
			imageSizeChangeListeners.clear();
		}
		if (magicWandToleranceChangeListeners != null) {
			magicWandToleranceChangeListeners.clear();
		}
	}
	
	public static interface ImageSizeChangeListener {
		
		void onImageSizeChange(ImageSizeState.ZoomState zoomState);
		
	}
	
	public static interface MagicWandToleranceChangeListener {
		
		void onMagicWandToleranceChange(byte newTolerance);
		
	}
	
}
