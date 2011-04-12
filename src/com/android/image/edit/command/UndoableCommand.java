package com.android.image.edit.command;

public interface UndoableCommand<P> extends Command<P> {
	
	void undo();

}
