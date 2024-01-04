package server.main;

import java.util.ArrayList;

public class GameData {

	private String gameID = "";
	private ArrayList<String> playerIDs = new ArrayList<String>();

	public ArrayList<String> getPlayerIDs() {
		return new ArrayList<String>(playerIDs);
	}


	public String getGameID() {
		return gameID;
	}

	public void setGameID(String gameID) {
		this.gameID = gameID;
	}


	public void addPlayer(String playerID) {
		playerIDs.add(playerID);
		
	}

}
