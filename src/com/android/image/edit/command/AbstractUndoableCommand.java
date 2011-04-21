package com.android.image.edit.command;

public abstract class AbstractUndoableCommand<T> extends AbstractCommand<T> implements UndoableCommand {

	public AbstractUndoableCommand(T target) {
		super(target);
	}
	
}
