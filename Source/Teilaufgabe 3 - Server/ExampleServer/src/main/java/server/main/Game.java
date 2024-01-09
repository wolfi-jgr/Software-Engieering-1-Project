package server.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		setHasChanged(true);
		if (playerToAct == null) {
			playerToAct = chooseRandomPlayer(getPlayers());
			playerToAct.setPlayerState(EPlayerGameState.MustAct);
			return;
		}

		for (Player eachPlayer : getPlayers()) {
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

	public FullMap getFullMap() {

		return data.getFullMap(); //////////////////////
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
		FullMap fullMap = new FullMap();
		Random random = new Random();
		if (random.nextBoolean()) {
			fullMap = mergeToSquareMap(data.getHalfMaps());
		} else {
			fullMap = mergeToLongMap(data.getHalfMaps());
		}

		//// merge

		saveFullMap(fullMap);

	}

	private FullMap mergeToLongMap(Map<String, PlayerHalfMap> halfMaps) {

		ArrayList<FullMap> fullMaps = getFullMapFromHalfMapNodes(halfMaps);

		if (fullMaps != null) {
			//System.out.println(fullMaps.size());
			return fullMaps.get(0);

		} else {
			return new FullMap();

		}

	}

	private ArrayList<FullMap> getFullMapFromHalfMapNodes(Map<String, PlayerHalfMap> halfMaps) {

		if (halfMaps == null) {
			throw new GenericExampleException("halfMapsNullException",
					"halfMaps were null when trying to convert them into FullMap");
		}

		ArrayList<FullMap> listToReturn = new ArrayList<FullMap>();

		for (PlayerHalfMap eachPlayerHalfMap : halfMaps.values()) {
			ArrayList<FullMapNode> fullMapNodes = new ArrayList<FullMapNode>();
			for (int x = 0; x < MAX_HALFMAP_WIDTH; x++) {
				for (int y = 0; y < MAX_HALFMAP_HEIGHT; y++) {

					// System.out.println(x + " " + y);

					for (PlayerHalfMapNode eachHalfMapNode : eachPlayerHalfMap.getMapNodes()) {
						if (x == eachHalfMapNode.getX() && y == eachHalfMapNode.getY()) {
							boolean isFort = eachHalfMapNode.isFortPresent();
							if (isFort) {
								fullMapNodes.add(new FullMapNode(eachHalfMapNode.getTerrain(),
										EPlayerPositionState.MyPlayerPosition, ETreasureState.NoOrUnknownTreasureState,
										EFortState.MyFortPresent, eachHalfMapNode.getX(), eachHalfMapNode.getY()));
							} else {
								fullMapNodes.add(new FullMapNode(eachHalfMapNode.getTerrain(),
										EPlayerPositionState.NoPlayerPresent, ETreasureState.NoOrUnknownTreasureState,
										EFortState.NoOrUnknownFortState, eachHalfMapNode.getX(),
										eachHalfMapNode.getY()));
							}
						}
					}

				}
			}
			listToReturn.add(new FullMap(fullMapNodes));

			///////////////////////////////////////////////////////////////
			break;
///////////////////////////////////////////////////////////////
		}
		return listToReturn;
	}

	private FullMap mergeToSquareMap(Map<String, PlayerHalfMap> halfMaps) {
		ArrayList<FullMap> fullMaps = getFullMapFromHalfMapNodes(halfMaps);
		if (fullMaps != null) {
//			System.out.println(fullMaps.size());
			return fullMaps.get(0);

		} else {
			return new FullMap();

		}
	}

	private void saveFullMap(FullMap fullMap) {

		data.saveFullMap(fullMap);
	}

	public GameState getGameState() {
		return data.getGameState();
	}

	public void setGameStateID(String gameStateID) {
		data.setGameStateID(gameStateID);
	}

}
