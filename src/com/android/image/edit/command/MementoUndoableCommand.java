package com.android.image.edit.command;

import com.android.image.edit.memento.Memento;
import com.android.image.edit.memento.MementoOriginator;

public class MementoUndoableCommand<M extends Memento, O extends MementoOriginator<M>> extends AbstractUndoableCommand<O> {
	
	private M state;
	
	private AbstractCommand<O> wrapped;
	
	public MementoUndoableCommand(AbstractCommand<O> wrapped) {
		super(wrapped.getTarget());
		this.wrapped = wrapped;
	}
	
	@Override
	public void execute(Object... params) {
		state = wrapped.getTarget().createMemento();
		wrapped.execute(params);
	}
	
	@Override
	public void undo() {
		wrapped.getTarget().setMemento(state);
		state.recycle();
	}

}
