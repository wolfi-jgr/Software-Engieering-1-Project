package server.main;

import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.EPlayerGameState;

public class Player {
	
	private EPlayerGameState playerState = EPlayerGameState.MustWait;
	private PlayerRegistration playerRegistration = new PlayerRegistration();
	private String playerID = "";
	private boolean sentMaptoServer = false;
//	private long playerTime = 0;
	private boolean sentHalfMapFlag = false; 
	
	
	public Player(String uniquePlayerID, PlayerRegistration playerRegistration) {
		
		this.playerID = uniquePlayerID;
		this.playerRegistration = playerRegistration;
//		this.playerTime = System.currentTimeMillis();
		
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
		playerState = state;
		
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

}
