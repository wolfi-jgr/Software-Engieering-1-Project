package server.eachgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import server.exceptions.GenericExampleException;
import server.player.Player;

public class Game {

	private final int GAME_ID_LENGTH = 5;

	private final int MAX_HALFMAP_WIDTH = 10;
	private final int MAX_HALFMAP_HEIGHT = 5;

	private GameData data = new GameData();
	private Player playerToAct;

	public Game() {
		data.setGameID(getRandomGameID());
	}

	private String getRandomGameID() {
		String randomGameID = "";
		Random rand = new Random();

		for (int i = 0; i < GAME_ID_LENGTH; i++) {
			char randomChar = (char) (rand.nextInt(26) + 97);
			randomGameID += randomChar;
		}
		return randomGameID;
	}

	public String getGameID() {
		return this.data.getGameID();
	}

	public int getPlayerCount() {
		return this.data.getPlayers().size();
	}

	public void addPlayer(Player playerToAdd) {
		this.data.addPlayer(playerToAdd);
	}

	public void setNextPlayerToAct() {
		if (data.isGameFinished()) {
			setHasChanged(true);
			return;
		}
		setHasChanged(true);
		if (playerToAct == null) {
			playerToAct = chooseRandomPlayer(getPlayers());
			playerToAct.setPlayerState(EPlayerGameState.MustAct);
			return;
		}
		for (Player eachPlayer : getPlayers()) {
			if(isFinished()) {
				
			}
			if (!playerToAct.equals(eachPlayer)) {
				playerToAct.setPlayerState(EPlayerGameState.MustWait);
				playerToAct = eachPlayer;
				playerToAct.setPlayerState(EPlayerGameState.MustAct);
				return;
			}
		}
	}

	public void setHasChanged(boolean changed) {
		data.setHasChanged(changed);
	}

	public boolean hasChanged() {
		return data.hasChanged();
	}

	private Player chooseRandomPlayer(Set<Player> players) {
		Random random = new Random();
		int randomNumber = random.nextInt(getPlayerCount());

		Object[] playerArray = players.toArray();

		if (randomNumber == 0) {
			return (Player) playerArray[0];
		} else {
			return (Player) playerArray[1];
		}
	}

	public Set<Player> getPlayers() {
		return data.getPlayers();
	}

	public FullMap getFullMap(String playerID) {
		return data.getFullMap(playerID);
	}

	public String getGameStateID() {
		return data.getGameStateID();
	}

	public void addHalfMap(PlayerHalfMap halfMap) {
		data.addHalfMap(halfMap);
	}

	public int getHalfMapCount() {
		return data.getHalfMapCount();
	}

	public void mergeHalfMaps() {
		Random random = new Random();
		boolean square = random.nextBoolean();

		for (Player eachPlayer : getPlayers()) {
			if (square) {
				setFullMap(mergeToSquareMap(data.getHalfMaps(), eachPlayer.getPlayerID()), eachPlayer.getPlayerID());
			} else {
				setFullMap(mergeToLongMap(data.getHalfMaps(), eachPlayer.getPlayerID()), eachPlayer.getPlayerID());
			}
		}
	}

	private void setFullMap(FullMap fullMap, String playerID) {
		data.setFullMap(fullMap, playerID);

	}

	private FullMap mergeToSquareMap(Map<String, PlayerHalfMap> halfMaps, String playerID) {
		return mergeMap(halfMaps, playerID, false);
	}

	private FullMap mergeMap(Map<String, PlayerHalfMap> halfMaps, String playerID, boolean isLongMap) {
		Map<String, FullMap> fullMaps = getFullMapFromHalfMapNodes(halfMaps, playerID);

		if (fullMaps != null) {
			ArrayList<FullMapNode> listOfFullMapNodes = new ArrayList<FullMapNode>();
			boolean skipFirst = true;

			for (Entry<String, FullMap> eachFullMap : fullMaps.entrySet()) {
				if (skipFirst) {
					listOfFullMapNodes.addAll(eachFullMap.getValue().getMapNodes());
					skipFirst = false;
				} else {
					for (FullMapNode eachFullMapNode : eachFullMap.getValue().getMapNodes()) {
						if (isLongMap) {
							listOfFullMapNodes.add(new FullMapNode(eachFullMapNode.getTerrain(),
									eachFullMapNode.getPlayerPositionState(), eachFullMapNode.getTreasureState(),
									eachFullMapNode.getFortState(), eachFullMapNode.getX() + MAX_HALFMAP_WIDTH,
									eachFullMapNode.getY()));
						} else {
							listOfFullMapNodes.add(new FullMapNode(eachFullMapNode.getTerrain(),
									eachFullMapNode.getPlayerPositionState(), eachFullMapNode.getTreasureState(),
									eachFullMapNode.getFortState(), eachFullMapNode.getX(),
									eachFullMapNode.getY() + MAX_HALFMAP_HEIGHT));
						}
					}
				}
			}
			return new FullMap(listOfFullMapNodes);

		} else {
			return new FullMap();
		}
	}

	private FullMap mergeToLongMap(Map<String, PlayerHalfMap> halfMaps, String playerID) {
		return mergeMap(halfMaps, playerID, true);
	}

	private Map<String, FullMap> getFullMapFromHalfMapNodes(Map<String, PlayerHalfMap> halfMaps, String playerID) {
		if (halfMaps == null) {
			throw new GenericExampleException("halfMapsNullException",
					"halfMaps were null when trying to convert them into FullMap");
		}

		Map<String, FullMap> mapToReturn = new HashMap<String, FullMap>();
		boolean hasToPutEnemyPlayer = true;
		for (Entry<String, PlayerHalfMap> eachPlayerHalfMap : halfMaps.entrySet()) {

			ArrayList<FullMapNode> fullMapNodes = new ArrayList<FullMapNode>();
			Player eachPlayer = getPlayer(eachPlayerHalfMap.getValue().getUniquePlayerID());

			for (PlayerHalfMapNode eachHalfMapNode : eachPlayerHalfMap.getValue().getMapNodes()) {
				boolean isFort = eachHalfMapNode.isFortPresent();

				if (isFort && eachPlayerHalfMap.getKey().equals(playerID)) {
					fullMapNodes.add(new FullMapNode(eachHalfMapNode.getTerrain(),
							EPlayerPositionState.MyPlayerPosition, ETreasureState.NoOrUnknownTreasureState,
							EFortState.MyFortPresent, eachHalfMapNode.getX(), eachHalfMapNode.getY()));
				} else {
					if (eachHalfMapNode.getTerrain() == ETerrain.Grass && hasToPutEnemyPlayer) {
						fullMapNodes.add(new FullMapNode(eachHalfMapNode.getTerrain(),
								EPlayerPositionState.EnemyPlayerPosition, ETreasureState.NoOrUnknownTreasureState,
								EFortState.NoOrUnknownFortState, eachHalfMapNode.getX(), eachHalfMapNode.getY()));
						hasToPutEnemyPlayer = false;
					} else {
						fullMapNodes.add(new FullMapNode(eachHalfMapNode.getTerrain(),
								EPlayerPositionState.NoPlayerPresent, ETreasureState.NoOrUnknownTreasureState,
								EFortState.NoOrUnknownFortState, eachHalfMapNode.getX(), eachHalfMapNode.getY()));

					}
				}
			}

			FullMap convertedHalfMap = new FullMap(fullMapNodes);
			mapToReturn.put(eachPlayer.getPlayerID(), convertedHalfMap);
			eachPlayer.setConvertedHalfMap(convertedHalfMap);
		}
		return mapToReturn;
	}

	private Player getPlayer(String uniquePlayerID) {

		for (Player eachPlayer : getPlayers()) {
			if (eachPlayer.getPlayerID().equals(uniquePlayerID)) {
				return eachPlayer;
			}
		}
		throw new GenericExampleException("PlayerNotFound",
				"the player with playerID: " + uniquePlayerID + " was not found.");
	}

	public GameState getGameState() {
		return data.getGameState();
	}

	public void setGameStateID(String gameStateID) {
		data.setGameStateID(gameStateID);
	}

	public void setLoser(String uniquePlayerID) {
		getPlayer(uniquePlayerID).setPlayerState(EPlayerGameState.Lost);
		getOtherPlayer(uniquePlayerID).setPlayerState(EPlayerGameState.Won);
		data.setEndOfGame();
	}

	public Player getOtherPlayer(String uniquePlayerID) {
		for (Player eachPlayer : getPlayers()) {
			if (!eachPlayer.getPlayerID().equals(uniquePlayerID)) {
				return eachPlayer;
			}
		}
		throw new GenericExampleException("PlayerNotFound", "the player was not found");
	}

	public boolean isFinished() {
		return data.isGameFinished();
	}
}
