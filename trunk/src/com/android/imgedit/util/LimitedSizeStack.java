package com.android.imgedit.util;

import java.util.LinkedList;

public class LimitedSizeStack<T> {
	
	private int maxSize;
	
	private LinkedList<T> linkedList = new LinkedList<T>();
	
	public LimitedSizeStack(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public void push(T element) {
		if (linkedList.size() == maxSize) {
			linkedList.removeFirst();
		}
		linkedList.addLast(element);
	}
	
	public T pop() {
		return linkedList.removeLast();
	}
	
	public int size() {
		return linkedList.size();
	}

}
