package android.image.editor.tool;

import android.graphics.Canvas;
import android.image.editor.ImageEditorView;

public interface Tool {
	
	void touchStart(ImageEditorView context, float x, float y);
	
	void touchMove(ImageEditorView context, float x, float y);
	
	void touchUp(ImageEditorView context);
	
	void onDraw(ImageEditorView context, Canvas canvas);

}
