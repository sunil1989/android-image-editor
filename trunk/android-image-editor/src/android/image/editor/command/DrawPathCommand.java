package android.image.editor.command;

import android.image.editor.ImageEditorView;
import android.image.editor.ImageEditorView.ImageEditorViewMemento;
import android.image.editor.tool.EraseTool;

public class DrawPathCommand implements UndoableCommand {
	
	private ImageEditorView target;
	
	private ImageEditorViewMemento state;

	public DrawPathCommand(ImageEditorView target) {
		this.target = target;
	}

	@Override
	public void execute() {
		state = target.createMemento();
		((EraseTool)target.getCurrentTool()).drawPath(target);
	}

	@Override
	public void undo() {
		target.setMemento(state);
		state.recycle();
		target.invalidate();
	}

}
