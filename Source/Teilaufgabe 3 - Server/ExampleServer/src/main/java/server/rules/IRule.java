package server.rules;

import java.util.Set;

import messagesbase.messagesfromclient.PlayerHalfMap;
import server.eachgame.Game;
import server.player.Player;

public interface IRule {
	
	
	public void validateNewGame();
	public void validatePlayerRegistration(Set<Player> players);
	public void validateHalfMap(PlayerHalfMap playerHalfMap, Game game);
	public void validateState(Set<Player> players, String gameID);
	
	

}
