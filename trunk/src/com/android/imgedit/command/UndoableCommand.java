package com.android.imgedit.command;

public interface UndoableCommand<P> extends Command<P> {
	
	void undo();

}
