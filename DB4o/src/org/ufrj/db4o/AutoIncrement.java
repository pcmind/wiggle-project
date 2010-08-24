package org.ufrj.db4o;

public class AutoIncrement {

	private String className;
	
	private int currentValue;
	
	public AutoIncrement(){
		currentValue = 0;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}

	public int getCurrentValue() {
		return currentValue;
	}

}
