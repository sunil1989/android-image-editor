package com.android.image.edit.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LimitedSizeStack<T> {
	//http://www.hostedredmine.com/issues/24744 - iterate through copy of collection	
	//implements Iterable<T>{	
	
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
	
	/**
	 * http://www.hostedredmine.com/issues/24744 - iterate through copy of collection	
	 * @return the copy (snapshot) of the current list, so clients can safely iterate through it
	 * to be fully correct that list should be unmodifiable (since modifying it will not change the original list)
	 * but to avoid creation of extra objects with Collections.unmodifiableList() - it's just documented 
	 */
	public List<T> copy(){
		if (linkedList.size()==0) {
			return Collections.emptyList();
	}
		return new ArrayList<T>(linkedList);
	}
	
	public T removeFirst() {
		return linkedList.removeFirst();
	}
	
	public T getLast() {
		return linkedList.getLast();
	}
	
	

}
