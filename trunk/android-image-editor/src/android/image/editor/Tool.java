package android.image.editor;

import android.graphics.Canvas;
import android.image.editor.MainActivity.MyView;

public interface Tool {
	
	void touchStart(MyView context, float x, float y);
	
	void touchMove(MyView context, float x, float y);
	
	void touchUp(MyView context);
	
	void onDraw(MyView context, Canvas canvas);

}
