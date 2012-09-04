package com.android.image.edit.tool;

import android.graphics.Canvas;
import com.android.image.edit.ImageEditorView;
import com.android.image.edit.scroll.ImageScrollPresentState;

public class ScrollTool implements Tool {
	
	public float startX = 0; //track x from one ACTION_MOVE to the next
	public float startY = 0; //track y from one ACTION_MOVE to the next

	@Override
	public void onDraw(ImageEditorView context, Canvas canvas) {
	}

	@Override
	public void touchMove(ImageEditorView context, float x, float y) {
		// Calculate move update. This will happen many times
		// during the course of a single movement gesture.
		if (context.getScrollState() instanceof ImageScrollPresentState) {
			context.setScrollByXY(x - startX, y - startY); //move update x and y increment
			context.updateScreenToMatrices();
		startX = x; //reset initial values to latest
		startY = y;
		context.invalidate(); //force a redraw
	}
	}

	@Override
	public void touchStart(ImageEditorView context, float x, float y) {
		if (context.getScrollState() instanceof ImageScrollPresentState) {
			startX = x;
			startY = y;
		}
	}

	@Override
	public void touchUp(ImageEditorView context) {}

}
