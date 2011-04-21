package com.android.image.edit.command;

public interface UndoableCommand extends Command {
	
	void undo();

}
