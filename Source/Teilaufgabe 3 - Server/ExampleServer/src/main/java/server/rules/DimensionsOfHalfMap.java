package server.rules;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.eachgame.Game;
import server.exceptions.HalfMapDimensionException;
import server.player.Player;

public class DimensionsOfHalfMap implements IRule {
	
	private final static Logger logger = LoggerFactory.getLogger(DimensionsOfHalfMap.class);

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
		final int maxX = 10;
		final int maxY = 5;

		for (int x = 0; x < maxX; x++) {
			for (int y = 0; y < maxY; y++) {
				int found = 0;
				for (PlayerHalfMapNode eachNode : playerHalfMap.getMapNodes()) {
					if (eachNode.getX() == x && eachNode.getY() == y) {
						found++;
					}
				}
				if (found != 1) {
					logger.error("The player: " + playerHalfMap.getUniquePlayerID() + " has sent a HalfMap with the wrong dimensions.");
					throw new HalfMapDimensionException(playerHalfMap.getUniquePlayerID());
				}
			}
		}
	}

	@Override
	public void validateState(Set<Player> players, String gameID) {
		// TODO Auto-generated method stub

	}

}
