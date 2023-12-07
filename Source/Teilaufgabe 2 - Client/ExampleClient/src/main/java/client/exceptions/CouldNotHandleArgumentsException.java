package client.exceptions;

public class CouldNotHandleArgumentsException extends Exception {

	/**
	 * default serialID (made by IDE)
	 */
	private static final long serialVersionUID = 1L;

	public CouldNotHandleArgumentsException() {
		super();

	}

	public CouldNotHandleArgumentsException(String serverBaseUrl, String gameID) {
		super("Could not handle arguments [serverBaseUrl]" + serverBaseUrl + " and [gameID] " + gameID);
	}

}
