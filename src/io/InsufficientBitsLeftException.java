package io;

public class InsufficientBitsLeftException extends Exception {

	private static final long serialVersionUID = 1L;
	private int _available;
	
	public InsufficientBitsLeftException(int available) {
		super("Not enough bits lefts in bit source");
		_available = available;
	}
	
	public int available() {
		return _available;
	}
}