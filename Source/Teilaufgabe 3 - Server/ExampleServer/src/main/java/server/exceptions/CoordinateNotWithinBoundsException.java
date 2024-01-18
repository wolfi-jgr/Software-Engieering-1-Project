package server.exceptions;

public class CoordinateNotWithinBoundsException extends GenericExampleException {

	public CoordinateNotWithinBoundsException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
		// TODO Auto-generated constructor stub
	}

	public CoordinateNotWithinBoundsException(String uniquePlayerID) {
		super("CoordinateNotWithinBounds", "the player: " + uniquePlayerID + " has sent a map where at least one Coordinate is out of bounds.");
	}

}
