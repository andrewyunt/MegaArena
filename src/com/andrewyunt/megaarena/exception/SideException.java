package com.andrewyunt.megaarena.exception;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class SideException extends Exception {

	private static final long serialVersionUID = -5456990131822939373L;

	public SideException() {
		
		super("An exception occured while conducting an operation on a spawn.");
	}
	
	public SideException(String message) {
		
		super(message);
	}
}