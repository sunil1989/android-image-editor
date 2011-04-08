package com.android.imgedit.command;

import com.android.imgedit.memento.Memento;
import com.android.imgedit.memento.MementoOriginator;

public abstract class MementoUndoableCommand<P, M extends Memento, O extends MementoOriginator<M>> implements UndoableCommand<P> {
	
	protected O target;
	
	private M state;
	
	public MementoUndoableCommand(O target) {
		this.target = target;
	}
	
	protected abstract void doExecute(P... params);
	
	@Override
	public void execute(P... params) {
		state = target.createMemento();
		doExecute(params);
	}
	
	@Override
	public void undo() {
		target.setMemento(state);
		state.recycle();
	}

}
