package com.android.image.edit.command;

public interface MultiTargetCommand<T> extends Command {
	
	void execute(T target, Object... params);
	
}
