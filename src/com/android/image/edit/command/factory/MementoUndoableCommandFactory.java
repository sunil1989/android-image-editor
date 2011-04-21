package com.android.image.edit.command.factory;

import com.android.image.edit.FileBitmap;
import com.android.image.edit.FileBitmap.CanvasBitmapMemento;
import com.android.image.edit.command.AbstractCommand;
import com.android.image.edit.command.MementoUndoableCommand;

public class MementoUndoableCommandFactory implements CommandFactory<FileBitmap, AbstractCommand<FileBitmap>> {
	
	private CommandFactory<FileBitmap, ? extends AbstractCommand<FileBitmap>> wrapped;

	public MementoUndoableCommandFactory(CommandFactory<FileBitmap, ? extends AbstractCommand<FileBitmap>> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public AbstractCommand<FileBitmap> createCropCommand(FileBitmap target) {
		return new MementoUndoableCommand<CanvasBitmapMemento, FileBitmap>(wrapped.createCropCommand(target));
	}

	@Override
	public AbstractCommand<FileBitmap> createDrawPathCommand(FileBitmap target) {
		return new MementoUndoableCommand<CanvasBitmapMemento, FileBitmap>(wrapped.createDrawPathCommand(target));
	}

}
