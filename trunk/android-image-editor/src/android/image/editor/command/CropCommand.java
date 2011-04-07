package android.image.editor.command;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.image.editor.ImageEditorView;
import android.image.editor.ImageEditorView.ImageEditorViewMemento;
import android.image.editor.tool.SelectionTool;

public class CropCommand implements UndoableCommand {
	
	private ImageEditorView target;
	
	private ImageEditorViewMemento state;

	public CropCommand(ImageEditorView target) {
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
