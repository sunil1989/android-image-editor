package com.android.image.edit.command;

import com.android.image.edit.BitmapWrapper;

public class ImageEditCommandDecorator implements ImageEditCommand {
	
	protected ImageEditCommand wrapped;

	public ImageEditCommandDecorator(ImageEditCommand wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public void execute(BitmapWrapper target, Object... params) {
		wrapped.execute(target, params);
	}

	@Override
	public boolean isLongLasting() {
		return wrapped.isLongLasting();
	}

	@Override
	public boolean canExecOnDisplayedImage() {
		return wrapped.canExecOnDisplayedImage();
	}
	
}
