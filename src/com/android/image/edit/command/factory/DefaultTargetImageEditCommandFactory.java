package com.android.image.edit.command.factory;

import com.android.image.edit.BitmapWrapper;
import com.android.image.edit.command.DefaultTargetImageEditCommand;
import com.android.image.edit.command.DefaultTargetImageEditCommandImpl;

public class DefaultTargetImageEditCommandFactory implements ImageEditCommandFactory {
	
	private ImageEditCommandFactory wrapped;

	public DefaultTargetImageEditCommandFactory(ImageEditCommandFactory wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public DefaultTargetImageEditCommand createEraseCommand(BitmapWrapper defaultTarget) {
		return new DefaultTargetImageEditCommandImpl(wrapped.createEraseCommand(defaultTarget), defaultTarget);
	}
	
	@Override
	public DefaultTargetImageEditCommand createCropCommand(BitmapWrapper defaultTarget) {
		return new DefaultTargetImageEditCommandImpl(wrapped.createCropCommand(defaultTarget), defaultTarget);
	}

	@Override
	public DefaultTargetImageEditCommand createFloodFillCommand(BitmapWrapper defaultTarget) {
		return new DefaultTargetImageEditCommandImpl(wrapped.createFloodFillCommand(defaultTarget), defaultTarget);
	}
	
}
