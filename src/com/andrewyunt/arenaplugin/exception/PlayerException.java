package com.andrewyunt.arenaplugin.exception;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class PlayerException extends Exception {
	
	private static final long serialVersionUID = 4805896720720741044L;

	public PlayerException() {
		
		super("An exception occured while conducting an operation on a player.");
	}
	
	public PlayerException(String message) {
		
		super(message);
	}
}