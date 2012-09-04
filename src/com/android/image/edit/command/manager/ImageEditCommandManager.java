package com.android.image.edit.command.manager;

import com.android.image.edit.ImageEditorView;
import com.android.image.edit.command.ImageEditCommand;

public interface ImageEditCommandManager<C extends ImageEditCommand> {
	
	void executeCommand(ImageEditorView context, C command, Object... params);
	
	void undo(ImageEditorView context);
	
	boolean hasMoreUndo();

}
