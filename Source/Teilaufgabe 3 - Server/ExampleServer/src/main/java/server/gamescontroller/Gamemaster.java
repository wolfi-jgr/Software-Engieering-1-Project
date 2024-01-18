package server.gamescontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import server.eachgame.Game;
import server.exceptions.GenericExampleException;
import server.player.Player;
import server.rules.CastleAmountAndLocation;
import server.rules.CoordinatesWithinBounds;
import server.rules.DimensionsOfHalfMap;
import server.rules.HalfMapHas50Fields;
import server.rules.HalfMapTerrainCount;
import server.rules.IRule;
import server.rules.Max2PlayersPerGame;
import server.rules.WaitOrActRule;
import server.rules.WaterOnEdgesCount;

public class Gamemaster {
	private Map<String, Game> games = new HashMap<String, Game>();
	private List<String> listOfGameIDs = new ArrayList<String>();

	private List<IRule> rulesForHalfMap = new ArrayList<IRule>(Arrays.asList(new HalfMapHas50Fields(),
			new HalfMapTerrainCount(), new CastleAmountAndLocation(), new DimensionsOfHalfMap(),
			new WaterOnEdgesCount(), new CoordinatesWithinBounds(), new WaitOrActRule()));
	private List<IRule> rulesForPlayers = new ArrayList<IRule>(Arrays.asList(new Max2PlayersPerGame()));

	private int MAX_NUMBER_OF_GAMES = 99;

	private final static Logger logger = LoggerFactory.getLogger(Gamemaster.class);

	private void checkGameID(UniqueGameIdentifier gameID) {
		if (gameID.getUniqueGameID().equals(null) || gameID.equals(new UniqueGameIdentifier())) {
			logger.error("the given gameID was null or empty.");
			throw new GenericExampleException("GameIDNullOrEmpty", "the given gameID was null or empty.");
		}
		if (games.get(gameID.getUniqueGameID()) == null) {
			logger.error("the given gameID was not found.");
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
		logger.debug("new game with gameID: " + newGame.getGameID() + " has been created.");
		return newGame;
	}

	private void removeFirstGame() {
		String gameIDOfFirstGame = listOfGameIDs.remove(0);
		logger.debug("game with gameID: " + gameIDOfFirstGame + " hhas been removed");
		games.remove(gameIDOfFirstGame);
	}

	public void addPlayer(UniqueGameIdentifier gameID, String uniquePlayerID, PlayerRegistration playerRegistration) {
		Game gameToAdd = getGame(gameID.getUniqueGameID());
		gameToAdd.addPlayer(new Player(uniquePlayerID, playerRegistration));
		logger.debug("player: " + uniquePlayerID + " has been added to game: " + gameID.getUniqueGameID());

		if (gameToAdd.getPlayerCount() == 2) {
			gameToAdd.setNextPlayerToAct();
		}

	}

	private Game getGame(String gameID) {
		Game gameToReturn = games.get(gameID);
		if (gameToReturn == null) {
			logger.error("the given gameID was not found.");
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
		logger.error("the player: " + uniquePlayerID + " in the game with id: " + gameID + " was not found.");
		throw new GenericExampleException("PlayerNotFound",
				"the player: " + uniquePlayerID + " in the game with id: " + gameID + " was not found.");
	}

	public void handleHalfMapRequest(UniqueGameIdentifier gameID, PlayerHalfMap halfmap) {
		checkGameID(gameID);
		checkPlayerID(gameID, halfmap.getUniquePlayerID());
		checkIfBothPlayersRegistered(gameID);
		checkIfPlayerHasSentMap(gameID, halfmap.getUniquePlayerID());
		checkHalfMapRules(halfmap, rulesForHalfMap, getGame(gameID.getUniqueGameID()));
		saveHalfMap(gameID, halfmap);

		getGame(gameID.getUniqueGameID()).setNextPlayerToAct();
		checkIfBothPlayersSentHalfMaps(gameID);
	}

	private void checkIfPlayerHasSentMap(UniqueGameIdentifier gameID, String uniquePlayerID) {
		Game gameToCheck = getGame(gameID.getUniqueGameID());
		for (Player eachPlayer : gameToCheck.getPlayers()) {
			if (uniquePlayerID.equals(eachPlayer.getPlayerID())) {
				if (eachPlayer.hasSentMap()) {
					logger.error("the player: " + uniquePlayerID + " has already sent a halfMap.");
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
		if (gameToCheck.bothPlayersSentHalfMaps()) {
			logger.debug("halfMaps being merged for game: " + gameID.getUniqueGameID());
			gameToCheck.mergeHalfMaps();
		}
	}

	private void checkIfBothPlayersRegistered(UniqueGameIdentifier gameID) {
		Game gameToCheck = getGame(gameID.getUniqueGameID());
		if (gameToCheck.bothPlayersRegistered()) {
			return;
		}
		logger.error("not both players were registered but one client tried to send map.");
		throw new GenericExampleException("NotBothPlayersRegistered",
				"not both players were registered but one client tried to send map.");
	}

	private void saveHalfMap(UniqueGameIdentifier gameID, PlayerHalfMap halfMap) {
		Game gameToSave = getGame(gameID.getUniqueGameID());
		gameToSave.addHalfMap(halfMap);
	}

	private void checkHalfMapRules(PlayerHalfMap halfMap, List<IRule> rules, Game gameToValidate) {
		for (IRule ruleToCheck : rules) {
			ruleToCheck.validateHalfMap(halfMap, gameToValidate);
		}
	}

	public void handlePlayersRequest(UniqueGameIdentifier gameID) {
		checkGameID(gameID);

		Game gameToHandle = getGame(gameID.getUniqueGameID());
		if (gameToHandle == null) {
			logger.error("the game with the id: " + gameID + " was not found.");
			throw new GenericExampleException("NoSuchGameFound", "the game with the id: " + gameID + " was not found.");
		}

		for (var eachRule : rulesForPlayers) {
			eachRule.validatePlayerRegistration(gameToHandle.getPlayers());
		}
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
