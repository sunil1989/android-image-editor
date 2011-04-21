package com.android.image.edit.tool;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.android.image.edit.ImageEditorView;

public class ScrollTool implements Tool {
	
	public float startX = 0; //track x from one ACTION_MOVE to the next
	public float startY = 0; //track y from one ACTION_MOVE to the next

	@Override
	public void crop(ImageEditorView context) {}

	@Override
	public void onDraw(ImageEditorView context, Canvas canvas) {
	}

	@Override
	public void touchMove(ImageEditorView context, MotionEvent event) {
		// Calculate move update. This will happen many times
		// during the course of a single movement gesture.
		float x = event.getRawX();
		float y = event.getRawY();
		context.setScrollByX(x - startX); //move update x increment
		context.setScrollByY(y - startY); //move update y increment
		startX = x; //reset initial values to latest
		startY = y;
		context.invalidate(); //force a redraw
	}

	@Override
	public void touchStart(ImageEditorView context, MotionEvent event) {
		startX = event.getRawX();
		startY = event.getRawY();
	}

	@Override
	public void touchUp(ImageEditorView context) {}

}
