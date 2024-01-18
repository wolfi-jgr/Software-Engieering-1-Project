package server.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.FullMap;
import server.main.ServerEndpoints;

public class Player {

	private EPlayerGameState playerState = EPlayerGameState.MustWait;
	private PlayerRegistration playerRegistration = new PlayerRegistration();
	private String playerID = "";
	private boolean sentMaptoServer = false;
	private boolean sentHalfMapFlag = false;
	private FullMap convertedHalfMap = new FullMap();
	private FullMap fullMap = new FullMap();
	
	private final static Logger logger = LoggerFactory.getLogger(Player.class);

	public Player(String uniquePlayerID, PlayerRegistration playerRegistration) {

		this.playerID = uniquePlayerID;
		this.playerRegistration = playerRegistration;

	}

	public boolean isSentMaptoServer() {
		return sentMaptoServer;
	}

	public void setSentMaptoServer(boolean sentMaptoServer) {
		this.sentMaptoServer = sentMaptoServer;
	}

	public EPlayerGameState getPlayerState() {
		return playerState;
	}

	public String getPlayerID() {
		return playerID;
	}

	public void setPlayerState(EPlayerGameState state) {
		this.playerState = state;
	}

	public PlayerRegistration getPlayerRegistration() {
		return this.playerRegistration;
	}

	public boolean hasSentMap() {
		return sentHalfMapFlag;
	}

	public void setSentHalfMap() {
		sentHalfMapFlag = true;
	}

	public void setConvertedHalfMap(FullMap convertedHalfMap) {
		this.convertedHalfMap = convertedHalfMap;
	}

	public FullMap getSideOfMap() {
		return this.convertedHalfMap;
	}

	public FullMap getFullMap() {
		return this.fullMap;
	}

	public void setFullMap(FullMap fullMapToSave) {
		this.fullMap = fullMapToSave;
	}

}
