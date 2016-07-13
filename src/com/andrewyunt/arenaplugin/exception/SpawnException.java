package com.andrewyunt.arenaplugin.exception;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class SpawnException extends Exception {

	private static final long serialVersionUID = -6853909759432090337L;

	public SpawnException() {
		
		super("An exception occured while conducting an operation on a spawn.");
	}
	
	public SpawnException(String message) {
		
		super(message);
	}
}