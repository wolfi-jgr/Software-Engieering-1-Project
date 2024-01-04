package server.rules;

import java.util.Set;

import server.main.Player;

public interface IRule {
	
	
	public void validateNewGame();
	public void validatePlayerRegistration(Set<Player> players);
	public void validateHalfMap();
	public void validateState();
	
	

}
