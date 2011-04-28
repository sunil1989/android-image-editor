package com.android.image.edit.command;

import android.graphics.Path;

import com.android.image.edit.BitmapWrapper;

public class EraseCommand extends AbstractMultiTargetCommand<BitmapWrapper> {

	public EraseCommand(BitmapWrapper target) {
		super(target);
	}

	@Override
	public void execute(BitmapWrapper target, Object... params) {
		if (!(params[0] instanceof Path)) {
			throw new IllegalArgumentException("android.graphics.Path expected but was " + params[0].getClass());
		}
		if (!(params[1] instanceof Float)) {
			throw new IllegalArgumentException("java.lang.Float expected but was " + params[1].getClass());
		}
		target.drawPath((Path)params[0], (Float)params[1]);
	}

}
