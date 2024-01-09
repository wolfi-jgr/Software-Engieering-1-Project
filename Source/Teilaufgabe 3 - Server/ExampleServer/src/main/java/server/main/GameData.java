package server.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.GameState;

public class GameData {

	private String gameID = "";
	private Set<Player> players = new HashSet<Player>();
	private FullMap fullMap = new FullMap();
	private Map<String, PlayerHalfMap> halfMaps = new HashMap<String, PlayerHalfMap>();

	private GameState gameState;
	private String gameStateID = UUID.randomUUID().toString();
	private boolean changed = false;

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

	public FullMap getFullMap() {
		return this.fullMap;
	}

	public void setFullMap(FullMap fullMapToSave) {
		this.fullMap = fullMapToSave;
	}

	public void addHalfMap(PlayerHalfMap halfMap) {

		halfMaps.put(halfMap.getUniquePlayerID(), halfMap);

	}

	public int getHalfMapCount() {
		return this.halfMaps.size();

	}

	public void saveFullMap(FullMap fullMap) {
		this.fullMap = fullMap;
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

}
