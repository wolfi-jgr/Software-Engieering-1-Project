package server.rules;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import messagesbase.messagesfromclient.PlayerHalfMap;
import server.eachgame.Game;
import server.exceptions.GenericExampleException;
import server.exceptions.HalfMaphasNot50FieldsException;
import server.player.Player;

public class HalfMapHas50Fields implements IRule{
	private final static Logger logger = LoggerFactory.getLogger(HalfMapHas50Fields.class);

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
			logger.error("");
			throw new HalfMaphasNot50FieldsException(playerHalfMap);
		}
		
	}

	@Override
	public void validateState(Set<Player> players, String gameID) {
		// TODO Auto-generated method stub
		
	}


}