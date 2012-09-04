package com.android.image.edit.command.manager;

import java.util.Iterator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.android.image.edit.BitmapWrapper;
import com.android.image.edit.ImageEditorView;
import com.android.image.edit.MemoryBitmap;
import com.android.image.edit.task.ImageEditUserTask;
import com.android.image.edit.task.UndoUserTask;
import com.android.image.edit.command.DefaultTargetImageEditCommand;
import com.android.image.edit.util.LimitedSizeStack;

public class DefaultTargetImageEditCommandManager implements ImageEditCommandManager<DefaultTargetImageEditCommand> {
	
	private LimitedSizeStack<CommandInvocation> commandHistory;
	private boolean imageChanged = false;
	private CommandUserTask commandUserTask;
	private UndoUserTask undoUserTask;
	
	public DefaultTargetImageEditCommandManager(int maxCommandHistorySize) {
		commandHistory = new LimitedSizeStack<CommandInvocation>(maxCommandHistorySize);
	}
	
	public DefaultTargetImageEditCommandManager() {
		this(1);
	}
	
	@Override
	public void executeCommand(ImageEditorView context, DefaultTargetImageEditCommand command, Object... params) {
		CommandInvocation pendingCommandInvocation = commandHistory.push(new CommandInvocation(command, params));
		if (!command.canExecOnDisplayedImage() && 
				((pendingCommandInvocation != null && pendingCommandInvocation.getCommand().isLongLasting()) ||
						hasLongLastingPendingCommands())) {
			commandUserTask = new CommandUserTask(context);
			commandUserTask.execute(pendingCommandInvocation);
		} else {
			if (pendingCommandInvocation != null) {
				pendingCommandInvocation.invoke();
			}
			if (!command.canExecOnDisplayedImage()) {
				context.updateDisplayableBitmap();
				context.invalidate();
			}
		}
		if (pendingCommandInvocation != null) {
			imageChanged = true;
		}
	}
	
	private boolean isTaskRunning(AsyncTask<?, ?, ?> task) {
		return task != null && task.getStatus() == AsyncTask.Status.RUNNING;
	}
	
	public boolean isTaskRunning() {
		return isTaskRunning(commandUserTask) || isTaskRunning(undoUserTask);
	}

	//TODO: Question to Kiril - why in this method we don't remove commands form the stack ?
	//method applyPendingCommandsInPlace remove items form the stack
	public Bitmap applyPendingCommands(ImageEditorView context, Bitmap initialOriginalBitmap, boolean original) {
		MemoryBitmap currentOriginalBitmap = new MemoryBitmap(context, initialOriginalBitmap, original);
		//http://www.hostedredmine.com/issues/24744 - iterate through copy of collection
		for (Iterator<CommandInvocation> iterator = commandHistory.copy().iterator(); iterator.hasNext(); ) {
			iterator.next().invoke(currentOriginalBitmap);
		}
		return currentOriginalBitmap.getBitmap();
	}
	
	public void applyPendingCommandsInPlace() {
		while (commandHistory.size() != 0) {
			commandHistory.removeFirst().invoke();
		}
	}

	@Override
	public boolean hasMoreUndo() {
		return commandHistory.size() > 0;
	}

	@Override
	public void undo(ImageEditorView context) {
		commandHistory.pop();
		if (hasLongLastingPendingCommands()) {
			undoUserTask = new UndoUserTask(context);
			undoUserTask.execute();
		} else {
			context.updateDisplayableBitmap();
			context.invalidate();
		}
	}
	
	public void popCommand(ImageEditorView context) {
		commandHistory.pop();
	}
	
	public boolean isImageChanged() {
		return imageChanged || hasMoreUndo();
	}
	
	public void setImageChanged(boolean imageChanged) {
		this.imageChanged = imageChanged;
	}
	
	public Object[] getLastCommandParams() {
		return commandHistory.getLast().getParams();
	}
	
	public boolean hasLongLastingPendingCommands() {
		//http://www.hostedredmine.com/issues/24744 - iterate through copy of collection
		for (CommandInvocation commandInvocation : commandHistory.copy()) {
			if (commandInvocation.getCommand().isLongLasting()) {
				return true;
			}
		}
		return false;
	}

	private static class CommandInvocation {
		
		private DefaultTargetImageEditCommand command;
		private Object[] params;
		
		public CommandInvocation(DefaultTargetImageEditCommand command, Object[] params) {
			this.command = command;
			this.params = params;
		}
		
		public void invoke() {
			command.execute(params);
		}
		
		public void invoke(BitmapWrapper target) {
			command.execute(target, params);
		}

		public Object[] getParams() {
			return params;
		}

		public DefaultTargetImageEditCommand getCommand() {
			return command;
		}
		
	}
	
	private static class CommandUserTask extends ImageEditUserTask<CommandInvocation> {

		public CommandUserTask(ImageEditorView context) {
			super(context, "Applying changes...");
		}

		@Override
		public Void doInBackgroundWithActivity(Activity activity, CommandInvocation... params) {
			CommandInvocation pendingCommandInvocation = params[0];
			if (pendingCommandInvocation != null) {
				pendingCommandInvocation.invoke();
			}
			return super.doInBackgroundWithActivity(activity, params);
		}

		@Override
		public void onPostExecute(Activity activity, Void result) {
			super.onPostExecute(activity, result);
			context = null;
		}
		
	}
	
}
