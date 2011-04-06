package android.image.editor;

import android.image.editor.MainActivity1.MyView;
import android.image.editor.MainActivity1.MyView.MyViewMemento;

public class DrawPathCommand implements Command {
	
	private MyView target;
	
	private MyViewMemento state;

	public DrawPathCommand(MyView target) {
		this.target = target;
	}

	@Override
	public void execute() {
		state = target.createMemento();
		target.drawPath();
	}

	@Override
	public void unexecute() {
		target.setMemento(state);
		state.recycle();
		target.invalidate();
	}

}
