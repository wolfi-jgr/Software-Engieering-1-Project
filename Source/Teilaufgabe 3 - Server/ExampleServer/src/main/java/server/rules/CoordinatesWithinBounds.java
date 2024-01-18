package server.rules;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.eachgame.Game;
import server.exceptions.CoordinateNotWithinBoundsException;
import server.player.Player;

public class CoordinatesWithinBounds implements IRule {

	private final static Logger logger = LoggerFactory.getLogger(CoordinatesWithinBounds.class);

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
		final int minX = 0;
		final int minY = 0;
		final int maxX = 9;
		final int maxY = 5;

		for (PlayerHalfMapNode eachNode : playerHalfMap.getMapNodes()) {
			if (eachNode.getX() < minX) {
				logger.error("the player: " + playerHalfMap.getUniquePlayerID()
						+ " has sent a map where at least one Coordinate is out of bounds.");
				throw new CoordinateNotWithinBoundsException(playerHalfMap.getUniquePlayerID());
			}
			if (eachNode.getX() > maxX) {
				logger.error("the player: " + playerHalfMap.getUniquePlayerID()
						+ " has sent a map where at least one Coordinate is out of bounds.");
				throw new CoordinateNotWithinBoundsException(playerHalfMap.getUniquePlayerID());
			}
			if (eachNode.getY() < minY) {
				logger.error("the player: " + playerHalfMap.getUniquePlayerID()
						+ " has sent a map where at least one Coordinate is out of bounds.");
				throw new CoordinateNotWithinBoundsException(playerHalfMap.getUniquePlayerID());
			}
			if (eachNode.getY() > maxY) {
				logger.error("the player: " + playerHalfMap.getUniquePlayerID()
						+ " has sent a map where at least one Coordinate is out of bounds.");
				throw new CoordinateNotWithinBoundsException(playerHalfMap.getUniquePlayerID());
			}
		}

	}

	@Override
	public void validateState(Set<Player> players, String gameID) {
		// TODO Auto-generated method stub

	}

}
