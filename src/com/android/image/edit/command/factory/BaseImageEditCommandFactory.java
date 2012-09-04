package com.android.image.edit.command.factory;


import com.android.image.edit.BitmapWrapper;
import com.android.image.edit.command.CropCommand;
import com.android.image.edit.command.EraseCommand;
import com.android.image.edit.command.FloodFillCommand;
import com.android.image.edit.command.ImageEditCommand;

public class BaseImageEditCommandFactory implements ImageEditCommandFactory {

	@Override
	public ImageEditCommand createEraseCommand(BitmapWrapper defaultTarget) {
		return new EraseCommand();
	}
	
	@Override
	public ImageEditCommand createCropCommand(BitmapWrapper defaultTarget) {
		return new CropCommand();
	}

	@Override
	public ImageEditCommand createFloodFillCommand(BitmapWrapper defaultTarget) {
		return new FloodFillCommand();
	}

}
