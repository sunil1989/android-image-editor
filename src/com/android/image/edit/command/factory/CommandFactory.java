package com.android.image.edit.command.factory;

import com.android.image.edit.command.AbstractCommand;

public interface CommandFactory<T, C extends AbstractCommand<T>> {
	
	C createDrawPathCommand(T target);
	
	C createCropCommand(T target);

}
