package com.android.image.edit.command;

import com.android.image.edit.BitmapWrapper;

public class DefaultTargetImageEditCommandImpl extends ImageEditCommandDecorator implements DefaultTargetImageEditCommand {
	
	private BitmapWrapper defaultTarget;
	
	public DefaultTargetImageEditCommandImpl(ImageEditCommand wrapped, BitmapWrapper defaultTarget) {
		super(wrapped);
		this.defaultTarget = defaultTarget;
	}

	public void execute(Object... params) {
		wrapped.execute(defaultTarget, params);
	}

}
