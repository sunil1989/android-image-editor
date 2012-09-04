package com.android.image.edit.task;

import android.app.Activity;
import com.android.image.edit.task.ImageEditUserTask;
import com.android.image.edit.ImageEditorView;

public class UndoUserTask extends ImageEditUserTask<Void> {

	public UndoUserTask(ImageEditorView context) {
		super(context, "Performing undo...");
	}

	@Override
	public void onPostExecute(Activity activity, Void result) {
		super.onPostExecute(activity, result);
		context = null;
	}
	
}
