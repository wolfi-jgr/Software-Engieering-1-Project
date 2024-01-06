package server.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.FullMap;

public class GameData {

	private String gameID = "";
	private Set<Player> players = new HashSet<Player>();
	private FullMap fullMap = new FullMap();
	private Map<String, PlayerHalfMap> halfMaps = new HashMap<String, PlayerHalfMap>();

	public Set<Player> getPlayers() {
		return players;
	}

	public String getGameID() {
		return gameID;
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

}
