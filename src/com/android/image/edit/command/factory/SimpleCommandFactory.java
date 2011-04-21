package com.android.image.edit.command.factory;

import com.android.image.edit.BitmapWrapper;
import com.android.image.edit.command.AbstractMultiTargetCommand;
import com.android.image.edit.command.CropCommand;
import com.android.image.edit.command.EraseCommand;

public class SimpleCommandFactory implements CommandFactory<BitmapWrapper, AbstractMultiTargetCommand<BitmapWrapper>> {
	
	private static SimpleCommandFactory instance;
	
	private SimpleCommandFactory() {}

	public static SimpleCommandFactory getInstance() {
		if (instance == null) {
			instance = new SimpleCommandFactory();
		}
		return instance;
	}

	@Override
	public AbstractMultiTargetCommand<BitmapWrapper> createCropCommand(BitmapWrapper target) {
		return new CropCommand(target);
	}

	@Override
	public AbstractMultiTargetCommand<BitmapWrapper> createDrawPathCommand(BitmapWrapper target) {
		return new EraseCommand(target);
	}

}
