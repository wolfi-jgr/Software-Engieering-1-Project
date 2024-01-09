package server.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import server.exceptions.GenericExampleException;
import server.rules.HalfMapHas50Fields;
import server.rules.IRule;
import server.rules.Max2PlayersPerGame;

public class Gamemaster {
	private Map<String, Game> games = new HashMap<String, Game>();
	private List<String> listOfGameIDs = new ArrayList<String>();
	private List<IRule> rulesForHalfMap = new ArrayList<IRule>(Arrays.asList(new HalfMapHas50Fields()));
	private int MAX_NUMBER_OF_GAMES = 99;

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
		if (games.size() == MAX_NUMBER_OF_GAMES) {
			removeFirstGame();
		}
		games.put(newGame.getGameID(), newGame);
		listOfGameIDs.add(newGame.getGameID());
		return newGame;
	}

	private void removeFirstGame() {
		String gameIDOfFirstGame = listOfGameIDs.remove(0);
		games.remove(gameIDOfFirstGame);
	}

	public void addPlayer(UniqueGameIdentifier gameID, String uniquePlayerID, PlayerRegistration playerRegistration) {
		Game gameToAdd = getGame(gameID.getUniqueGameID());
		gameToAdd.addPlayer(new Player(uniquePlayerID, playerRegistration));

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
		checkIfPlayerHasSentMap(gameID, halfmap.getUniquePlayerID());
		checkHalfMap(halfmap, rulesForHalfMap);
		saveHalfMap(gameID, halfmap);

		setRandomPlayerToActFirst(gameID);
		checkIfBothPlayersSentHalfMaps(gameID);
	}

	private void checkIfPlayerHasSentMap(UniqueGameIdentifier gameID, String uniquePlayerID) {
		Game gameToCheck = getGame(gameID.getUniqueGameID());
		for (Player eachPlayer : gameToCheck.getPlayers()) {
			if (uniquePlayerID.equals(eachPlayer.getPlayerID())) {
				if (eachPlayer.hasSentMap()) {
					throw new GenericExampleException("PlayerAlreadySentHalfMap",
							"the player: " + uniquePlayerID + " has already sent a halfMap.");
				} else {
					eachPlayer.setSentHalfMap();
				}
			}
		}
	}

	private void checkIfBothPlayersSentHalfMaps(UniqueGameIdentifier gameID) {
		Game gameToCheck = getGame(gameID.getUniqueGameID());
		if (gameToCheck.getHalfMapCount() == 2) {
			gameToCheck.mergeHalfMaps();
		}
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

	private void checkHalfMap(PlayerHalfMap halfMap, List<IRule> rules) {
		for (IRule ruleToCheck : rules) {
			ruleToCheck.validateHalfMap(halfMap);
		}
	}

	public void handlePlayersRequest(UniqueGameIdentifier gameID) {
		checkGameID(gameID);

		Game gameToHandle = getGame(gameID.getUniqueGameID());
		if (gameToHandle == null) {
			throw new GenericExampleException("NoSuchGameFound", "the game with the id: " + gameID + " was not found.");
		}
////////////////////////////
		Set<IRule> rules = new HashSet<IRule>();
		rules.add(new Max2PlayersPerGame());

		for (var eachRule : rules) {
			eachRule.validatePlayerRegistration(gameToHandle.getPlayers());
		}

		////////////////////////////
		gameToHandle.setHasChanged(true);
	}

	public GameState handleStateRequest(UniqueGameIdentifier gameID, String uniquePlayerID) {
		checkGameID(gameID);
		checkPlayerID(gameID, uniquePlayerID);
		Game gameToHandle = games.get(gameID.getUniqueGameID());
		GameState gameState = getGameStateForPlayer(gameToHandle, uniquePlayerID);
		return gameState;
	}

	private GameState getGameStateForPlayer(Game gameOfInterest, String playerID) {
		Set<PlayerState> setOfPlayerStates = new HashSet<PlayerState>();

		for (Player eachPlayer : gameOfInterest.getPlayers()) {
			if (eachPlayer.getPlayerID().equals(playerID)) {
				PlayerState playerState = new PlayerState(eachPlayer.getPlayerRegistration().getStudentFirstName(),
						eachPlayer.getPlayerRegistration().getStudentLastName(),
						eachPlayer.getPlayerRegistration().getStudentUAccount(), eachPlayer.getPlayerState(),
						new UniquePlayerIdentifier(eachPlayer.getPlayerID()), false);
				setOfPlayerStates.add(playerState);
			} else {
				PlayerState playerState = new PlayerState(eachPlayer.getPlayerRegistration().getStudentFirstName(),
						eachPlayer.getPlayerRegistration().getStudentLastName(),
						eachPlayer.getPlayerRegistration().getStudentUAccount(), eachPlayer.getPlayerState(),
						new UniquePlayerIdentifier("NOT ALLOWED"), false);
				setOfPlayerStates.add(playerState);
			}
		}

		String gameStateID = gameOfInterest.getGameStateID();
		if (gameOfInterest.hasChanged()) {
			gameStateID = UUID.randomUUID().toString();
			gameOfInterest.setGameStateID(gameStateID);
			gameOfInterest.setHasChanged(false);
		}
		GameState gameState = new GameState(gameOfInterest.getFullMap(playerID), setOfPlayerStates, gameStateID);
		return gameState;
	}

}
