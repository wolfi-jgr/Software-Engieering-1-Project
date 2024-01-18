package server.rules;

import java.util.Set;

import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.eachgame.Game;
import server.exceptions.WaterOnEdgeException;
import server.player.Player;

public class WaterOnEdgesCount implements IRule {

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
		final int leftX = 0;
		final int rightX = 9;
		final int upperY = 0;
		final int lowerY = 4;

		final int limitLeftAndRight = 2;
		final int limitUpperAndLower = 4;

		int countLeft = 0;
		int countRight = 0;
		int countUpper = 0;
		int countLower = 0;

		for (PlayerHalfMapNode eachNode : playerHalfMap.getMapNodes()) {
			if (eachNode.getTerrain() == ETerrain.Water) {
				if (eachNode.getX() == leftX) {
					countLeft++;
				}
				if (eachNode.getX() == rightX) {
					countRight++;
				}
				if (eachNode.getY() == upperY) {
					countUpper++;
				}
				if (eachNode.getY() == lowerY) {
					countLower++;
				}
			}
		}

		if (countLeft > limitLeftAndRight || countRight > limitLeftAndRight) {
			throw new WaterOnEdgeException(playerHalfMap.getUniquePlayerID());
		}
		if (countUpper > limitUpperAndLower || countLower > limitUpperAndLower) {
			throw new WaterOnEdgeException(playerHalfMap.getUniquePlayerID());
		}

	}

	@Override
	public void validateState(Set<Player> players, String gameID) {
		// TODO Auto-generated method stub

	}

}
