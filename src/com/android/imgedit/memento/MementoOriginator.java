package com.android.imgedit.memento;

public interface MementoOriginator<M extends Memento> {
	
	M createMemento();
	
	void setMemento(M memento);
	
}
