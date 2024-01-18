package server.rules;

import java.util.Set;

import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.eachgame.Game;
import server.exceptions.CastleCountAndOrLocationException;
import server.player.Player;

public class CastleAmountAndLocation implements IRule {

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

		int castleCount = 0;
		int castleOnGrassFieldCount = 0;

		for (PlayerHalfMapNode eachNode : playerHalfMap.getMapNodes()) {
			if (eachNode.isFortPresent()) {
				castleCount++;
				if (eachNode.getTerrain() == ETerrain.Grass) {
					castleOnGrassFieldCount++;
				}
			}
		}
		
		if(castleCount != 1 || castleOnGrassFieldCount != 1 ) {
			throw new CastleCountAndOrLocationException(castleCount, castleOnGrassFieldCount);
		}
	}

	@Override
	public void validateState(Set<Player> players, String gameID) {
		// TODO Auto-generated method stub

	}

}
