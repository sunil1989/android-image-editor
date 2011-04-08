package com.android.imgedit.command;

public interface Command<P> {
	
	void execute(P... params);
	
}
