package com.android.image.edit.command.manager;

import java.util.Iterator;

import android.graphics.Bitmap;

import com.android.image.edit.BitmapWrapper;
import com.android.image.edit.ImageEditorView;
import com.android.image.edit.MemoryBitmap;
import com.android.image.edit.command.AbstractMultiTargetCommand;
import com.android.image.edit.util.LimitedSizeStack;

public class CommandManagerImpl implements CommandManager<AbstractMultiTargetCommand<BitmapWrapper>> {
	
	private LimitedSizeStack<CommandInvocation<BitmapWrapper>> commandHistory;
	
	public CommandManagerImpl(int maxCommandHistorySize) {
		commandHistory = new LimitedSizeStack<CommandInvocation<BitmapWrapper>>(maxCommandHistorySize);
	}
	
	public CommandManagerImpl() {
		this(1);
	}
	
	@Override
	public void executeCommand(AbstractMultiTargetCommand<BitmapWrapper> command, Object... params) {
		CommandInvocation<BitmapWrapper> pendingCommand = commandHistory.push(new CommandInvocation<BitmapWrapper>(command, params));
		if (pendingCommand != null) {
			pendingCommand.invoke();
		}
	}

	@Override
	public Bitmap getCurrentOriginalBitmap(Bitmap initialOriginalBitmap) {
		MemoryBitmap currentOriginalCanvasBitmap = new MemoryBitmap(ImageEditorView.BACKGROUND_COLOR, initialOriginalBitmap);
		for (Iterator<CommandInvocation<BitmapWrapper>> iterator = commandHistory.iterator(); iterator.hasNext(); ) {
			iterator.next().invoke(currentOriginalCanvasBitmap);
		}
		return currentOriginalCanvasBitmap.getBitmap();
	}

	@Override
	public boolean hasMoreUndo() {
		return commandHistory.size() > 0;
	}

	@Override
	public void undo() {
		commandHistory.pop();
	}
	
	private static class CommandInvocation<T> {
		
		private AbstractMultiTargetCommand<T> command;
		private Object[] params;
		
		public CommandInvocation(AbstractMultiTargetCommand<T> command, Object[] params) {
			this.command = command;
			this.params = params;
		}
		
		public void invoke() {
			command.execute(params);
		}
		
		public void invoke(T target) {
			command.execute(target, params);
		}
		
	}
	
}
