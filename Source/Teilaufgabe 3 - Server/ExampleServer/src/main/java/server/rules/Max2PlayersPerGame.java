package server.rules;

import java.util.Set;

import messagesbase.messagesfromclient.PlayerHalfMap;
import server.eachgame.Game;
import server.exceptions.Already2PlayersRegisteredException;
import server.player.Player;

public class Max2PlayersPerGame implements IRule{

	@Override
	public void validateNewGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validatePlayerRegistration(Set<Player> players) {
		
		if(players.size() == 2) {
			throw new Already2PlayersRegisteredException();
		}
		
		
	}

	@Override
	public void validateHalfMap(PlayerHalfMap playerHalfMap, Game game) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateState(Set<Player> players, String gameID) {
		// TODO Auto-generated method stub
		
	}


	

}
