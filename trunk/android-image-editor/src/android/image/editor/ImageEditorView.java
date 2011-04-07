package android.image.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.image.editor.command.CommandManager;
import android.image.editor.command.CropCommand;
import android.image.editor.tool.EraseTool;
import android.image.editor.tool.SelectionTool;
import android.image.editor.tool.Tool;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;

public class ImageEditorView extends View {
	
	private Bitmap  mBitmap;
    private Canvas  mCanvas;
    private Paint   mBitmapPaint;
    private CommandManager commandManager = new CommandManager(2);
    private Tool currentTool = new EraseTool();

    public ImageEditorView(Context c) {
        super(c);

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/dress.jpg");
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawBitmap(bm, 0, 0, null);
        bm.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        currentTool.onDraw(this, canvas);
    }

    public CommandManager getCommandManager() {
		return commandManager;
	}
    
    public void drawBitmap(Canvas canvas) {
    	canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
    }

	@Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentTool.touchStart(this, x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                currentTool.touchMove(this, x, y);
                break;
            case MotionEvent.ACTION_UP:
                currentTool.touchUp(this);
                break;
        }
        return true;
    }
	
    public Canvas getCanvas() {
		return mCanvas;
	}

	public Tool getCurrentTool() {
		return currentTool;
	}

	public ImageEditorViewMemento createMemento() {
		return new ImageEditorViewMemento(mBitmap.copy(Bitmap.Config.ARGB_8888, false));
	}
	
	public void setMemento(ImageEditorViewMemento memento) {
		redrawBitmap(memento.state);
	}
	
	public void redrawBitmap(Bitmap bitmap) {
		mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		mCanvas.drawBitmap(bitmap, 0, 0, null);
	}
	
	public void undo() {
		commandManager.undo();
	}
	
	public void changeTool(Tool tool) {
		currentTool = tool;
	}
	
	public void crop() {
		if (!(currentTool instanceof SelectionTool)) {
			return;
		}
		SelectionTool selectionTool = (SelectionTool)currentTool;
		if (!selectionTool.selectionExists()) {
			return;
		}
		commandManager.executeCommand(new CropCommand(this));
	}
    
    public Bitmap getBitmap() {
		return mBitmap;
	}

	public void setBitmap(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
	}

	public static class ImageEditorViewMemento {
		
		private Bitmap state;

		private ImageEditorViewMemento(Bitmap state) {
			this.state = state;
		}
		
		public void recycle() {
			state.recycle();
		}
		
	}
	
}
