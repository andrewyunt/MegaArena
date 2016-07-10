package com.andrewyunt.arenaplugin.exception;

public class ArenaCreationException extends Exception {

	private static final long serialVersionUID = -4820918921383578042L;

	public ArenaCreationException() {
		
		super("An exception occured while creating the arena.");
	}
	
	public ArenaCreationException(String message) {
		
		super(message);
	}
}