package com.android.image.edit.command;

public interface Command<P> {
	
	void execute(P... params);
	
}
