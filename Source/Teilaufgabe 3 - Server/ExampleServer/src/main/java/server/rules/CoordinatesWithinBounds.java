package server.rules;

import java.util.Set;

import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.eachgame.Game;
import server.exceptions.CoordinateNotWithinBoundsException;
import server.player.Player;

public class CoordinatesWithinBounds implements IRule{

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
		final int minX=0;
		final int minY=0;
		final int maxX=9;
		final int maxY=5;
		
		for(PlayerHalfMapNode eachNode : playerHalfMap.getMapNodes()) {
			if(eachNode.getX() < minX) {
				throw new CoordinateNotWithinBoundsException(playerHalfMap.getUniquePlayerID());
			}
			if(eachNode.getX() > maxX) {
				throw new CoordinateNotWithinBoundsException(playerHalfMap.getUniquePlayerID());
			}
			if(eachNode.getY() < minY) {
				throw new CoordinateNotWithinBoundsException(playerHalfMap.getUniquePlayerID());
			}
			if(eachNode.getY() > maxY) {
				throw new CoordinateNotWithinBoundsException(playerHalfMap.getUniquePlayerID());
			}
		}
		
	}

	@Override
	public void validateState(Set<Player> players, String gameID) {
		// TODO Auto-generated method stub
		
	}

}
