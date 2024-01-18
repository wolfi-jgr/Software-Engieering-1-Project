package server.exceptions;

import messagesbase.messagesfromclient.PlayerHalfMap;

public class HalfMaphasNot50FieldsException extends GenericExampleException {

	public HalfMaphasNot50FieldsException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
		// TODO Auto-generated constructor stub
	}

	public HalfMaphasNot50FieldsException(PlayerHalfMap playerHalfMap) {
		super("HalfMaphasNot50FieldsException", "the player: " + playerHalfMap.getUniquePlayerID()
				+ " has sent a map with " + playerHalfMap.getMapNodes().size() + " fields..");
	}

}
