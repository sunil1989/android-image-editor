package com.android.image.edit.command;

import android.graphics.Path;

import com.android.image.edit.BitmapWrapper;

public class EraseCommand extends AbstractImageEditCommand {

	public EraseCommand() {
		super(false, true);
	}

	@Override
	public void execute(BitmapWrapper target, Object... params) {
		target.drawPath((Path)params[0], (Float)params[1]);
	}

}
