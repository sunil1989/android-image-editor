package com.android.image.edit.command.factory;

import com.android.image.edit.BitmapWrapper;
import com.android.image.edit.command.ImageEditCommand;

public interface ImageEditCommandFactory {
	
	ImageEditCommand createEraseCommand(BitmapWrapper defaultTarget);
	
	ImageEditCommand createCropCommand(BitmapWrapper defaultTarget);
	
	ImageEditCommand createFloodFillCommand(BitmapWrapper defaultTarget);

}
