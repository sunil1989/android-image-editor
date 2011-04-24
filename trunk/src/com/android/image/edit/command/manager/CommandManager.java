package com.android.image.edit.command.manager;

import android.graphics.Bitmap;

import com.android.image.edit.command.Command;

public interface CommandManager<C extends Command> {
	
	void executeCommand(C command, Object... params);
	
	void undo();
	
	boolean hasMoreUndo();
	
	Bitmap applyPendingCommands(Bitmap initialOriginalBitmap, boolean original, boolean needMakeCopy);

}
