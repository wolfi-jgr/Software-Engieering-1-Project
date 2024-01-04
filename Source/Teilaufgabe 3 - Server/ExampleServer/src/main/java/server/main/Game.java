package server.main;

import java.util.ArrayList;
import java.util.Random;

public class Game {

	private GameData data = new GameData();
	
	public Game() {
		
		data.setGameID(getRandomGameID(5));
		
	}

	private String getRandomGameID(int lengthOfGameID) {
		String randomGameID = "";
		Random rand = new Random();

		for (int i = 0; i < lengthOfGameID; i++) {
			char randomChar = (char) (rand.nextInt(26) + 97);
			randomGameID += randomChar;
		}

		return randomGameID;
	}
	public String getGameID() {
		return this.data.getGameID();
	}

	public int getPlayerCount() {
		
		return this.data.getPlayerIDs().size();
	}

	public void addPlayer(String uniquePlayerID) {
		this.data.addPlayer(uniquePlayerID);
	}

	public ArrayList<String> getPlayerIDs() {
		
		return this.data.getPlayerIDs();
	}

}
