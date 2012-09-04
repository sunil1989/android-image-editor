package com.android.image.edit.task;

import android.app.Activity;
import android.app.ProgressDialog;

public abstract class ProgressUserTask<A extends Activity, Params, Progress, Result> extends ActivityAwareUserTask<A, Params, Progress, Result> {
	private final String progressMessage;
	private ProgressDialog progressDialog;
	
	public ProgressUserTask(A activity) {
		this(activity, null);
	}

	public ProgressUserTask(A activity, final String progressMessage) {
		super(activity);
		this.progressMessage = progressMessage;
	}
	
	public ProgressUserTask(A activity, final int progressMessageResId) {
		this(activity, activity.getString(progressMessageResId));
	}

	/*public String getProgressMessage() {
		return progressMessage;
	}*/

    protected abstract void onPostExecute(final A activity, Result result);

	@Override
	public void onPreExecute() {
	    final A activity = getActivity();
	    if (activity != null) {
    		if (progressMessage != null && !activity.isFinishing()) {
    			showProgressDialog(activity);
    		}
	    }
	}

	public void showProgressDialog(final Activity activity) {
		if (progressMessage != null && !activity.isFinishing()){
			if (progressDialog != null && progressDialog.isShowing() && activity.equals(progressDialog.getOwnerActivity())) {
				progressDialog.dismiss();
			}
			progressDialog = ProgressDialog.show(activity, "", progressMessage);
		}
	}
	
	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}

	public void setProgressDialog(ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
	}
	protected void stopProgress(A activity){
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}		
	}
	@Override
	protected final void doPostExecuteWithActivity(A activity, Result result) {
		if (progressMessage != null){
			stopProgress(activity);
		}
		onPostExecute(activity, result);
	}

	public boolean isShowProgress() {
		return progressMessage != null;
	}
	
	/**
	 * Remove resources not used after calling activity is destroyed
	 * Should be called in activity.onDestroy()
	 */
	public void clean(){
		super.clean();
		if (progressDialog != null  && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
		progressDialog = null;
	}
	
	public void onPause(Activity activity){
	}

}
