package com.android.image.edit.memento;

public interface MementoOriginator<M extends Memento> {
	
	M createMemento();
	
	void setMemento(M memento);
	
}
