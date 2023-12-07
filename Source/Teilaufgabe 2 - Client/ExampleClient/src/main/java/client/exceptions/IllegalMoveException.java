package client.exceptions;

import client.positioning.Coordinate;

public class IllegalMoveException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public IllegalMoveException() {
		super();
	}

	public IllegalMoveException(Coordinate start, Coordinate end) {
		super("IllegalMoveException: " + start.toString() + "to" + end.toString());
	}
}