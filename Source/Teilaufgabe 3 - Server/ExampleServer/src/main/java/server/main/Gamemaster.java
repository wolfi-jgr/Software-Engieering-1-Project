package server.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import server.exceptions.GenericExampleException;
import server.rules.IRule;
import server.rules.Max2PlayersPerGame;

public class Gamemaster {
	private Map<String, Game> games = new HashMap<String, Game>();

	private List<IRule> rulesForHalfMap = new ArrayList<IRule>();

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
		while (games.containsKey(newGame.getGameID())) {
			newGame = new Game();
		}
		
//		if(games.size() == MAX_NUMBER_OF_GAMES) {remove() }
		games.put(newGame.getGameID(), newGame);
		return newGame;

	}

	public void addPlayer(UniqueGameIdentifier gameID, String uniquePlayerID) {
		Game gameToAdd = getGame(gameID.getUniqueGameID());
		gameToAdd.addPlayer(new Player(uniquePlayerID));

		if (gameToAdd.getPlayerCount() == 2) {
			gameToAdd.setNextPlayerToAct();
		}

	}

	private Game getGame(String gameID) {

		Game gameToReturn = games.get(gameID);

		if (gameToReturn == null) {
			throw new GenericExampleException("GameIDNotFound", "the given gameID was not found.");
		}
		return gameToReturn;

	}

	public void checkPlayerID(UniqueGameIdentifier gameID, String uniquePlayerID) {
		Game gameToValidate = getGame(gameID.getUniqueGameID());
		for (Player eachPlayer : gameToValidate.getPlayers()) {
			if (uniquePlayerID.equals(eachPlayer.getPlayerID())) {
				return;
			}
		}
		throw new GenericExampleException("PlayerNotFound",
				"the player: " + uniquePlayerID + " in the game with id: " + gameID + " was not found.");
	}

	public void handleHalfMapRequest(UniqueGameIdentifier gameID, PlayerHalfMap halfmap) {

		checkGameID(gameID);
		checkPlayerID(gameID, halfmap.getUniquePlayerID());
		checkIfBothPlayersRegistered(gameID);
		checkHalfMap(rulesForHalfMap);
		saveHalfMap(gameID, halfmap);// first check because maybe wrong

		setRandomPlayerToActFirst(gameID);

	}

	private void setRandomPlayerToActFirst(UniqueGameIdentifier gameID) {
		getGame(gameID.getUniqueGameID()).setNextPlayerToAct();
	}

	private void checkIfBothPlayersRegistered(UniqueGameIdentifier gameID) {
		Game gameToCheck = getGame(gameID.getUniqueGameID());
		if (gameToCheck.getPlayerCount() == 2) {
			return;
		}
		throw new GenericExampleException("NotBothPlayersRegistered",
				"not both players were registered but one client tried to send map.");

	}

	private void saveHalfMap(UniqueGameIdentifier gameID, PlayerHalfMap halfMap) {

		Game gameToSave = getGame(gameID.getUniqueGameID());
		gameToSave.addHalfMap(halfMap);

	}

	private void checkHalfMap(List<IRule> rules) {

		//////////////////////////////////////////////////

	}

	public void handlePlayersRequest(UniqueGameIdentifier gameID) {
		checkGameID(gameID);

		Game gameToHandle = getGame(gameID.getUniqueGameID());
		Set<IRule> rules = new HashSet<IRule>();
		rules.add(new Max2PlayersPerGame());

		for (var eachRule : rules) {
			eachRule.validatePlayerRegistration(gameToHandle.getPlayers());
		}

	}

	public GameState handleStateRequest(UniqueGameIdentifier gameID, String uniquePlayerID) {
		checkGameID(gameID);
		checkPlayerID(gameID, uniquePlayerID);
		Game gameToHandle = games.get(gameID.getUniqueGameID());
		GameState gameState = getGameStateFromPlayer(gameToHandle, uniquePlayerID);
		return gameState;
	}

	private GameState getGameStateFromPlayer(Game gameOfInterest, String playerID) {
		Set<PlayerState> setOfPlayerStates = new HashSet<PlayerState>();

		for (Player eachPlayer : gameOfInterest.getPlayers()) {
			if (playerID.equals(eachPlayer.getPlayerID())) {
				PlayerState playerState = new PlayerState(eachPlayer.getPlayerRegistration().getStudentFirstName(),
						eachPlayer.getPlayerRegistration().getStudentLastName(),
						eachPlayer.getPlayerRegistration().getStudentUAccount(), eachPlayer.getPlayerState(),
						new UniquePlayerIdentifier(playerID), false);
				setOfPlayerStates.add(playerState);
			}
		}

		GameState gameState = new GameState(gameOfInterest.getFullMap(), setOfPlayerStates, UUID.randomUUID().toString());

		return gameState;
	}

}
