package server.rules;

import java.util.Set;

import messagesbase.messagesfromclient.PlayerHalfMap;
import server.exceptions.GenericExampleException;
import server.main.Player;

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
	public void validateHalfMap(PlayerHalfMap playerHalfMap) {
		
		if(playerHalfMap.getMapNodes().size() != 50) {
			throw new GenericExampleException("HalfMaphasNot50Fields", "the sent HalfMap has not 50 fields.");
			
		}
		
	}

	@Override
	public void validateState() {
		// TODO Auto-generated method stub
		
	}


}