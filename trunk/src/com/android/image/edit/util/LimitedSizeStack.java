package com.android.image.edit.util;

import java.util.Iterator;
import java.util.LinkedList;

public class LimitedSizeStack<T> {
	
	private int maxSize;
	
	private LinkedList<T> linkedList = new LinkedList<T>();
	
	public LimitedSizeStack(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public T push(T element) {
		linkedList.addLast(element);
		if (linkedList.size() > maxSize) {
			return linkedList.removeFirst();
		}
		return null;
	}
	
	public T pop() {
		return linkedList.removeLast();
	}
	
	public int size() {
		return linkedList.size();
	}
	
	public Iterator<T> iterator() {
		return linkedList.iterator();
	}

}
