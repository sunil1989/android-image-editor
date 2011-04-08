package com.android.imgedit.command;

import com.android.imgedit.ImageEditorView;
import com.android.imgedit.ImageEditorView.ImageEditorViewMemento;

public class DrawPathCommand extends MementoUndoableCommand<Object, ImageEditorViewMemento, ImageEditorView> {

	public DrawPathCommand(ImageEditorView target) {
		super(target);
	}

	@Override
	public void doExecute(Object... params) {
		target.drawPath();
	}

}
