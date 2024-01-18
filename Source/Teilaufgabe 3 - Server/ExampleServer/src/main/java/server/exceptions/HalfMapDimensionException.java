package server.exceptions;

public class HalfMapDimensionException extends GenericExampleException {

	public HalfMapDimensionException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

	public HalfMapDimensionException(String uniquePlayerID) {
		super("HalfMapDimensionException", "The player: " + uniquePlayerID + " has sent a HalfMap with the wrong dimensions.");
	}

}
