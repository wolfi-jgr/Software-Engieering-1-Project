package server.rules;

import java.util.Set;

import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.eachgame.Game;
import server.exceptions.HalfMapDimensionException;
import server.player.Player;

public class DimensionsOfHalfMap implements IRule {

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
