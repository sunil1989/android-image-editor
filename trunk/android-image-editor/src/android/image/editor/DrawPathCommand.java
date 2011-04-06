package android.image.editor;

import android.image.editor.MainActivity.MyView;
import android.image.editor.MainActivity.MyView.MyViewMemento;

public class DrawPathCommand implements UndoableCommand {
	
	private MyView target;
	
	private MyViewMemento state;

	public DrawPathCommand(MyView target) {
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
