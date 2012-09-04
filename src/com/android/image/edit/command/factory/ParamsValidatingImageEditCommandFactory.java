package com.android.image.edit.command.factory;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import com.android.image.edit.BitmapWrapper;
import com.android.image.edit.command.ImageEditCommand;
import com.android.image.edit.command.ParamsValidatingImageEditCommand;


public class ParamsValidatingImageEditCommandFactory implements ImageEditCommandFactory {
	
	private ImageEditCommandFactory wrapped;

	public ParamsValidatingImageEditCommandFactory(ImageEditCommandFactory wrapped) {
		this.wrapped = wrapped;
	}
	
	@Override
	public ImageEditCommand createEraseCommand(BitmapWrapper defaultTarget) {
		return new ParamsValidatingImageEditCommand(new Class<?>[]{Path.class, Float.class}, wrapped.createEraseCommand(defaultTarget));
	}

	@Override
	public ImageEditCommand createCropCommand(BitmapWrapper defaultTarget) {
		return new ParamsValidatingImageEditCommand(new Class<?>[]{RectF.class}, wrapped.createCropCommand(defaultTarget));
	}

	@Override
	public ImageEditCommand createFloodFillCommand(BitmapWrapper defaultTarget) {
		return new ParamsValidatingImageEditCommand(new Class<?>[]{Point.class, Byte.class, Handler.class}, wrapped.createFloodFillCommand(defaultTarget));
	}

}
