package com.android.image.edit.tool;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.android.image.edit.ImageEditorView;

public interface Tool {
	
	void touchStart(ImageEditorView context, MotionEvent event);
	
	void touchMove(ImageEditorView context, MotionEvent event);
	
	void touchUp(ImageEditorView context);
	
	void onDraw(ImageEditorView context, Canvas canvas);
	
	void drawPath(ImageEditorView context);
	
	void crop(ImageEditorView context);

}
