package com.android.image.edit.command;

import com.android.image.edit.BitmapWrapper;

public class ParamsValidatingImageEditCommand extends ImageEditCommandDecorator {
	
	private Class<?>[] expectedParamsClasses;
	
	public ParamsValidatingImageEditCommand(Class<?>[] expectedParamsClasses, ImageEditCommand wrapped) {
		super(wrapped);
		this.expectedParamsClasses = expectedParamsClasses;
	}
	
	private void validateParams(Object... actualParams) {
		if (expectedParamsClasses.length != actualParams.length) {
			throw new IllegalArgumentException("Illegal argument count. Expected " + expectedParamsClasses.length + " but was " + actualParams.length);
		}
		for (int i = 0; i < actualParams.length; i++) {
			if (!expectedParamsClasses[i].isAssignableFrom(actualParams[i].getClass())) {
				throw new IllegalArgumentException("Illegal class of argument with number " + i + "(0-based). Expected " + 
						expectedParamsClasses[i].getCanonicalName() + " but was " + actualParams[i].getClass().getCanonicalName());
			}
		}
	}

	@Override
	public void execute(BitmapWrapper target, Object... params) {
		validateParams(params);
		super.execute(target, params);
	}

}
