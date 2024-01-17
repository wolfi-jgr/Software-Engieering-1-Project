package server.exceptions;

import messagesbase.messagesfromclient.ETerrain;

public class TerrainCountException extends GenericExampleException {

	public TerrainCountException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

	public TerrainCountException(String playerID, ETerrain terrain, int value) {
		super("TerrainCountWasWrong",
				"the player: " + playerID + " sent a map with the wrong amount of " + terrain + ", value was " +value);
	}

}
