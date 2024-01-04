package server.rules;

import java.util.Set;

import server.exceptions.GenericExampleException;
import server.main.Player;

public class Max2PlayersPerGame implements IRule{

	@Override
	public void validateNewGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validatePlayerRegistration(Set<Player> players) {
		
		if(players.size() != 2) {
			throw new GenericExampleException("errorname", "errormessage");
		}
		
		
	}

	@Override
	public void validateHalfMap() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateState() {
		// TODO Auto-generated method stub
		
	}

}
