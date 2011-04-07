package android.image.editor.command;

import android.image.editor.LimitedSizeStack;

public class CommandManager {
	
	private LimitedSizeStack<UndoableCommand> commandHistory;
	
	public CommandManager(int maxCommandHistorySize) {
		commandHistory = new LimitedSizeStack<UndoableCommand>(maxCommandHistorySize);
	}
	
	public CommandManager() {
		this(1);
	}
	
	public void executeCommand(Command command) {
		command.execute();
		if (command instanceof UndoableCommand) {
			commandHistory.push((UndoableCommand)command);
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
