package com.android.image.edit.command;

import com.android.image.edit.ImageEditorView;
import com.android.image.edit.ImageEditorView.ImageEditorViewMemento;

public class DrawPathCommand extends MementoUndoableCommand<Object, ImageEditorViewMemento, ImageEditorView> {

	public DrawPathCommand(ImageEditorView target) {
		super(target);
	}

	@Override
	public void doExecute(Object... params) {
		target.drawPath();
	}

}
