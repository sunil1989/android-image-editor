package com.android.image.edit.task;


import android.app.Activity;
import android.os.AsyncTask;

public abstract class ActivityAwareUserTask<A extends Activity, Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	
	private A activity;

	public ActivityAwareUserTask(A activity) {
		super();
		this.activity = activity;
	}

	@SuppressWarnings("unchecked")
	public void setActivity(Activity activity) {
		this.activity = (A)activity;
	}
	
	protected abstract Result doInBackgroundWithActivity(A activity, Params... params);
	
	protected A getActivity() {
	    return activity;
	}
	
	@Override
	public Result doInBackground(Params... params) {
		A savedActivity = activity;
		if (savedActivity != null) {
			return doInBackgroundWithActivity(savedActivity, params);
		}
		cancel(false);
		return null;
	}

	@Override
	final public void onPostExecute(Result result) {
		if (isCancelled()) return;
	    final A activity = this.activity;
	    this.activity = null;
		if (activity != null && !activity.isFinishing()) {
	        doPostExecuteWithActivity(activity, result);
		}
	}
	
	public void clean(){
		this.activity = null;
	}
	
	protected void doPostExecuteWithActivity(A activity, Result result) {
		
	}
}