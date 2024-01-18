package server.exceptions;

public class WaterOnEdgeException extends GenericExampleException {

	public WaterOnEdgeException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

	public WaterOnEdgeException(String uniquePlayerID) {
		super("", "the player: " + uniquePlayerID + " has sent a HalfMap with too much water fields on edges");
	}

}
