package server.exceptions;

public class PlayerMustWaitException extends GenericExampleException {

	public PlayerMustWaitException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

	public PlayerMustWaitException(String uniquePlayerID) {

		super("PlayerMustWaitException",
				"the player: " + uniquePlayerID + " has sent a HalfMap but it was not his turn to send..");

	}

}
