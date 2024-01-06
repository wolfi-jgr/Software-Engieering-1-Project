package server.main;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.FullMap;

public class Game {

	private final int GAME_ID_LENGTH = 5;

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

		return new FullMap(); //////////////////////
	}

	public Object getGameStateID() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addHalfMap(PlayerHalfMap halfMap) {
		data.addHalfMap(halfMap);
	}

}
