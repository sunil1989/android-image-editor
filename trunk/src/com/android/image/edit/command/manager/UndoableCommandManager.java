package com.android.image.edit.command.manager;

import android.graphics.Bitmap;

import com.android.image.edit.command.AbstractCommand;
import com.android.image.edit.command.AbstractUndoableCommand;
import com.android.image.edit.command.UndoableCommand;
import com.android.image.edit.util.LimitedSizeStack;

public class UndoableCommandManager<T> implements CommandManager<AbstractCommand<T>> {
	
	private LimitedSizeStack<AbstractUndoableCommand<T>> commandHistory;
	
	public UndoableCommandManager(int maxCommandHistorySize) {
		commandHistory = new LimitedSizeStack<AbstractUndoableCommand<T>>(maxCommandHistorySize);
	}
	
	public UndoableCommandManager() {
		this(1);
	}
	
	@Override
	public void executeCommand(AbstractCommand<T> command, Object... params) {
		command.execute(params);
		if (command instanceof UndoableCommand) {
			commandHistory.push((AbstractUndoableCommand<T>)command);
		}
	}
	
	@Override
	public void undo() {
		if (commandHistory.size() > 0) {
			commandHistory.pop().undo();
		}
	}
	
	@Override
	public boolean hasMoreUndo() {
		return commandHistory.size() > 0;
	}

	@Override
	public Bitmap getCurrentOriginalBitmap(Bitmap initialOriginalBitmap) {
		return initialOriginalBitmap;
	}

}
