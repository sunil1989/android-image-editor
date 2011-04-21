package com.android.image.edit.command;

public abstract class AbstractMultiTargetCommand<T> extends AbstractCommand<T> implements MultiTargetCommand<T> {

	public AbstractMultiTargetCommand(T target) {
		super(target);
	}

	@Override
	public void execute(Object... params) {
		execute(target, params);
	}
	
}
