package server.exceptions;

public class Already2PlayersRegisteredException extends GenericExampleException {

	public Already2PlayersRegisteredException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

	public Already2PlayersRegisteredException() {
		super("Already2PlayersRegistered", "there are already 2 players registered for this game");
	}
	

}
