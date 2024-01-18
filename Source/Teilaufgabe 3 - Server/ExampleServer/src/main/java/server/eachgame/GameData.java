package server.eachgame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.GameState;
import server.exceptions.GenericExampleException;
import server.player.Player;

public class GameData {

	private String gameID = "";
	private Set<Player> players = new HashSet<Player>();
	private Map<String, PlayerHalfMap> halfMaps = new HashMap<String, PlayerHalfMap>();

	private GameState gameState;
	private String gameStateID = UUID.randomUUID().toString();
	private boolean changed = false;
	private boolean hasGameEnded = false;

	public GameState getGameState() {

		return this.gameState;
	}

	public void setGameState(GameState gameStateToSet) {
		this.gameState = gameStateToSet;
	}

	public Set<Player> getPlayers() {
		return this.players;
	}

	public String getGameID() {
		return this.gameID;
	}

	public void setGameID(String gameID) {
		this.gameID = gameID;
	}

	public void addPlayer(Player playerToAdd) {
		players.add(playerToAdd);
	}

	public FullMap getFullMap(String playerID) {
		return getPlayer(playerID).getFullMap();
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

	public void setFullMap(FullMap fullMapToSave, String playerID) {
		getPlayer(playerID).setFullMap(fullMapToSave);
	}

	public void addHalfMap(PlayerHalfMap halfMap) {

		halfMaps.put(halfMap.getUniquePlayerID(), halfMap);

	}

	public int getHalfMapCount() {
		return this.halfMaps.size();
	}


	public Map<String, PlayerHalfMap> getHalfMaps() {
		return halfMaps;
	}

	public String getGameStateID() {
		return this.gameStateID ;
	}

	public void setHasChanged(boolean changed) {
		this.changed  = changed;
	}
	
	public boolean hasChanged() {
		return changed;
	}

	public void setGameStateID(String gameStateID) {
		this.gameStateID = gameStateID;
	}

	public void setEndOfGame() {
		this.hasGameEnded  = true;
	}
	
	public boolean isGameFinished() {
		return this.hasGameEnded;
	}

}
