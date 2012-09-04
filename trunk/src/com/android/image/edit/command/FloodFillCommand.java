package com.android.image.edit.command;

import java.util.Stack;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import com.android.image.edit.BitmapWrapper;

public final class FloodFillCommand extends AbstractImageEditCommand {
	
	public static final int DEFAULT_TOLERANCE = 20;
	private static final int ERASE_COLOR = 0x00FFFFFF;
	private static final int TOUCH_TOLERANCE = 5;
	private static final int MAXIMUM_COMMAND_EXECUTION_TIME = 12000; //12 seconds
	
	private int oldColor;
	private byte tolerance;
	private boolean stopped = false;

	public FloodFillCommand() {
		super(true, false);
	}

	@Override
	public void execute(BitmapWrapper target, Object... params) {
		stopped = false;
		Point startPoint = (Point)params[0];
		tolerance = (Byte)params[1];
		Handler handler = (Handler)params[2];
		final Bitmap bitmap = target.getBitmap();
		int left = Math.max(startPoint.x - TOUCH_TOLERANCE, 0);
		int right = Math.min(startPoint.x + TOUCH_TOLERANCE, bitmap.getWidth()-1);
		int top = Math.max(startPoint.y - TOUCH_TOLERANCE, 0);
		int bottom = Math.min(startPoint.y + TOUCH_TOLERANCE, bitmap.getHeight()-1);
		int redSum = 0, greenSum = 0, blueSum = 0, n = 0;
		for (int x = left; x <= right; x++) {
			for (int y = top; y <= bottom; y++) {
				int color = bitmap.getPixel(x, y);
				if (Math.hypot(x - startPoint.x, y - startPoint.y) < TOUCH_TOLERANCE && Color.alpha(color) != 0) {
					redSum += Color.red(color);
					greenSum += Color.green(color);
					blueSum += Color.blue(color);
					n++;
				}
			}
		}
		oldColor = Color.rgb(Math.round(redSum*1.f/n), Math.round(greenSum*1.f/n), Math.round(blueSum*1.f/n));
		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();
		int[]  pixels = new int[width*height];
	    bitmap.getPixels(pixels,0,width,0,0,width,height);

		Stack<Integer> stack = new Stack<Integer>();
	    
	    int y1;
	    boolean spanLeft, spanRight;
	    
	    stack.push(startPoint.x);
	    stack.push(startPoint.y);
	    final Runnable delayedRunnable = new Runnable() {
			@Override
			public void run() {
				stopped = true;
				
			}
		};
	    handler.postDelayed(delayedRunnable, MAXIMUM_COMMAND_EXECUTION_TIME);
	    while(!stopped && !stack.isEmpty()) {
	    	int y = stack.pop();
	    	int x = stack.pop();
	        y1 = y;
	        while (y1 >= 0 && x + y1*width < pixels.length && targetColor(pixels[x + y1*width])) {
	        	--y1;
	        }
	        ++y1;
	        spanLeft = spanRight = false;
	        while (y1 < height && x + y1*width < pixels.length && targetColor(pixels[x + y1*width])) {
	        	pixels[x+y1*width] = ERASE_COLOR;
	            if (!spanLeft && x > 0 && targetColor(pixels[x - 1 + y1*width])) {
	                stack.push(x - 1);
	                stack.push(y1);
	                spanLeft = true;
	            }
	            else if (spanLeft && x > 0 && targetColor(pixels[x - 1 + y1*width])) {
	                spanLeft = false;
	            }
	            if (!spanRight && x < width - 1 && targetColor(pixels[x + 1 + y1*width])) {
	                stack.push(x + 1);
	                stack.push(y1);
	                spanRight = true;
	            } else if (spanRight && x < width - 1 && targetColor(pixels[x + 1 + y1*width])){
	                spanRight = false;
	            } 
	            ++y1;
	        }
	    }
	    handler.removeCallbacks(delayedRunnable);
	    bitmap.setPixels(pixels,0,width,0,0,width,height);
	    pixels = null;
		target.setBitmap(bitmap, null);
	}
	
	
	private boolean targetColor(final int color) {
		return Color.alpha(color)!=0 && 
		Math.abs(Color.red(color) - Color.red(oldColor)) <= tolerance &&
		Math.abs(Color.green(color) - Color.green(oldColor)) <= tolerance &&
		Math.abs(Color.blue(color) - Color.blue(oldColor)) <= tolerance;
	}
	
}
