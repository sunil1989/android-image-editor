package com.android.image.edit.command;

import com.android.image.edit.BitmapWrapper;

public interface ImageEditCommand {
	
	void execute(BitmapWrapper target, Object... params);
	
	boolean isLongLasting();
	
	boolean canExecOnDisplayedImage();
	
}
