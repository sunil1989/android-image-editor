package com.android.imgedit.tool;

import android.graphics.Canvas;
import com.android.imgedit.ImageEditorView;

public interface Tool {
	
	void touchStart(ImageEditorView context, float x, float y);
	
	void touchMove(ImageEditorView context, float x, float y);
	
	void touchUp(ImageEditorView context);
	
	void onDraw(ImageEditorView context, Canvas canvas);
	
	void drawPath(ImageEditorView context);
	
	void crop(ImageEditorView context);

}
