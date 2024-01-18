package server.rules;

import java.util.Set;

import messagesbase.messagesfromclient.PlayerHalfMap;
import server.eachgame.Game;
import server.exceptions.GenericExampleException;
import server.player.Player;

public class HalfMapHas50Fields implements IRule{

	@Override
	public void validateNewGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validatePlayerRegistration(Set<Player> players) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateHalfMap(PlayerHalfMap playerHalfMap, Game game) {
		
		if(playerHalfMap.getMapNodes().size() != 50) {
			throw new GenericExampleException("HalfMaphasNot50Fields", "the sent HalfMap has not 50 fields.");
		}
		
	}

	@Override
	public void validateState(Set<Player> players, String gameID) {
		// TODO Auto-generated method stub
		
	}


}