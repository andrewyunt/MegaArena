package com.andrewyunt.megaarena.exception;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class GameException extends Exception {

	private static final long serialVersionUID = 4563943878994607477L;

	public GameException() {
		
		super("An exception occured while conducting an operation on a game.");
	}
	
	public GameException(String message) {
		
		super(message);
	}
}