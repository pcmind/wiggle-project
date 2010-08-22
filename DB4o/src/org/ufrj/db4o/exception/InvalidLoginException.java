package org.ufrj.db4o.exception;

public class InvalidLoginException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6434906015190415250L;

	public InvalidLoginException() {
		super();
		
	}
	
	public InvalidLoginException(String arg0) {
		super(arg0);
		
	}
	
	public InvalidLoginException(Throwable arg0) {
		super(arg0);
		
	}
	
	public InvalidLoginException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		
	}
}
