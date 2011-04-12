package com.android.image.edit.command;

import com.android.image.edit.util.LimitedSizeStack;

public class CommandManager {
	
	private LimitedSizeStack<UndoableCommand<?>> commandHistory;
	
	public CommandManager(int maxCommandHistorySize) {
		commandHistory = new LimitedSizeStack<UndoableCommand<?>>(maxCommandHistorySize);
	}
	
	public CommandManager() {
		this(1);
	}
	
	public <P> void executeCommand(Command<P> command, P... params) {
		command.execute(params);
		if (command instanceof UndoableCommand<?>) {
			commandHistory.push((UndoableCommand<?>)command);
		}
	}
	
	public void undo() {
		if (commandHistory.size() > 0) {
			commandHistory.pop().undo();
		}
	}
	
	public boolean hasMoreUndo() {
		return commandHistory.size() > 0;
	}

}
