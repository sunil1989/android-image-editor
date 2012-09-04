package com.android.image.edit.task;

import android.app.Activity;
import com.android.image.edit.ImageEditorView;

public class ImageEditUserTask<Params> extends ProgressUserTask<Activity, Params, Void, Void> {
	
	protected ImageEditorView context;
	private boolean imageSizeChanged;
	
	public ImageEditUserTask(ImageEditorView context, String progressMessage) {
		super((Activity)context.getContext(), progressMessage);
		this.context = context;
	}
	
	@Override
	public Void doInBackgroundWithActivity(Activity activity, Params... params) {
		imageSizeChanged = context.updateDisplayableBitmap(false);
		return null;
	}
	
	@Override
	public void onPostExecute(Activity activity, Void result) {
		if (imageSizeChanged) {
			context.updateUIAfterImageSizeChange();
		}
		context.invalidate();
	}

}
