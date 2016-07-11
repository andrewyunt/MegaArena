package com.andrewyunt.arenaplugin.exception;

public class ArenaException extends Exception {

	private static final long serialVersionUID = -4820918921383578042L;

	public ArenaException() {
		
		super("An exception occured while conducting an operation on an arena.");
	}
	
	public ArenaException(String message) {
		
		super(message);
	}
}