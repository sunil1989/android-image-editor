package com.android.image.edit.command;

public abstract class AbstractCommand<T> implements Command {
	
	protected T target;

	public AbstractCommand(T target) {
		this.target = target;
	}

	public T getTarget() {
		return target;
	}
	
}
