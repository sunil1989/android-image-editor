package com.android.image.edit.command;

public abstract class AbstractImageEditCommand implements ImageEditCommand {
	
	private boolean longLasting;
	private boolean canExecOnDisplayedImage;

	public AbstractImageEditCommand(boolean longLasting, boolean canExecOnDisplayedImage) {
		this.longLasting = longLasting;
		this.canExecOnDisplayedImage = canExecOnDisplayedImage;
	}

	@Override
	public boolean isLongLasting() {
		return longLasting;
	}
	
	@Override
	public boolean canExecOnDisplayedImage() {
		return canExecOnDisplayedImage;
	}
	
}
