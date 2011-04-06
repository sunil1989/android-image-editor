package android.image.editor;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.image.editor.MainActivity.MyView;
import android.image.editor.MainActivity.MyView.MyViewMemento;

public class CropCommand implements UndoableCommand {
	
	private MyView target;
	
	private MyViewMemento state;

	public CropCommand(MyView target) {
		super();
		this.target = target;
	}

	@Override
	public void execute() {
		state = target.createMemento();
		SelectionTool selectionTool = (SelectionTool)target.getCurrentTool();
		RectF selection = selectionTool.getSelection();
		selectionTool.clearSelection();
		Bitmap croppedBitmap = Bitmap.createBitmap(target.getBitmap(), Math.round(selection.left), Math.round(selection.top), Math.round(selection.width()), Math.round(selection.height()));
		target.redrawBitmap(croppedBitmap);
		croppedBitmap.recycle();
		target.invalidate();
	}

	@Override
	public void undo() {
		target.setMemento(state);
		state.recycle();
		target.invalidate();
	}
	
}
