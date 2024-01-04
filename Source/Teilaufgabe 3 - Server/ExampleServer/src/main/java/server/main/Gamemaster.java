package server.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import messagesbase.UniqueGameIdentifier;
import server.exceptions.GenericExampleException;

public class Gamemaster {
	Map<String, Game> games = new HashMap<String, Game>();

	// ArrayList<Game> games = new ArrayList<Game>();

	private void checkGameID(UniqueGameIdentifier gameID) {
		if (gameID.getUniqueGameID().equals(null) || gameID.equals(new UniqueGameIdentifier())) {
			throw new GenericExampleException("GameIDNullOrEmpty", "the given gameID was null or empty.");
		}

		if (games.get(gameID.getUniqueGameID()) == null) {
			throw new GenericExampleException("GameIDNotFound", "the given gameID was not found.");
		}


	}

	public Game createNewGame() {
		Game newGame = new Game();
		games.put(newGame.getGameID(), newGame);
		return newGame;

	}

	public void addPlayer(UniqueGameIdentifier gameID, String uniquePlayerID) {
		Game gameToAdd = getGame(gameID.getUniqueGameID());
		if (gameToAdd.getPlayerCount() < 2) {
			gameToAdd.addPlayer(uniquePlayerID);
		}

	}

	private Game getGame(String gameID) {
		
		Game gameToReturn = games.get(gameID);
			
		if(gameToReturn == null){
			throw new GenericExampleException("GameIDNotFound", "the given gameID was not found.");			
		}
		return gameToReturn;
		
		
	}

	public void checkPlayerID(UniqueGameIdentifier gameID, String uniquePlayerID) {
		Game gameToValidate = getGame(gameID.getUniqueGameID());
		for (String eachPlayerID : gameToValidate.getPlayerIDs()) {
			if (!uniquePlayerID.equals(eachPlayerID)) {
				throw new GenericExampleException("PlayerNotFound", "the player in the current game was not found.");
			}
		}
	}

	public void handleHalfMapRequest(UniqueGameIdentifier gameID, String uniquePlayerID) {

		checkGameID(gameID);
		checkPlayerID(gameID, uniquePlayerID);

	}

	public void handlePlayersRequest(UniqueGameIdentifier gameID) {
		checkGameID(gameID);

	}

	public void handleStateRequest(UniqueGameIdentifier gameID, String uniquePlayerID) {

		checkGameID(gameID);
		checkPlayerID(gameID, uniquePlayerID);

	}

}
