package server.rules;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.eachgame.Game;
import server.exceptions.GenericExampleException;
import server.exceptions.TerrainCountException;
import server.player.Player;

public class HalfMapTerrainCount implements IRule {

	private final int MIN_GRASS_COUNT = 24; // 48% from 50 fields
	private final int MIN_MOUNTAIN_COUNT = 5; // 10% from 50 fields
	private final int MIN_WATER_COUNT = 7; // 14% from 50 fields
	
	

	  private static final Logger logger = LoggerFactory.getLogger(HalfMapTerrainCount.class);

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

		int grassCount = 0;
		int mountainCount = 0;
		int waterCount = 0;

		for (PlayerHalfMapNode eachNode : playerHalfMap.getMapNodes()) {
			switch (eachNode.getTerrain()) {
			case Grass:
				grassCount++;
				break;
			case Mountain:
				mountainCount++;
				break;
			case Water:
				waterCount++;
				break;

			default:
				throw new GenericExampleException("TerrainNotRecognized",
						"The terrain in TerrainCountRule was not grass, mountain or water.");
			}
		}
	

		if (grassCount < MIN_GRASS_COUNT) {
			game.setLoser(playerHalfMap.getUniquePlayerID());
			throw new TerrainCountException(playerHalfMap.getUniquePlayerID(), ETerrain.Grass, grassCount);
		}
		if (mountainCount < MIN_MOUNTAIN_COUNT) {
			game.setLoser(playerHalfMap.getUniquePlayerID());
			throw new TerrainCountException(playerHalfMap.getUniquePlayerID(), ETerrain.Mountain, mountainCount);
		}
		if (waterCount < MIN_WATER_COUNT) {
			game.setLoser(playerHalfMap.getUniquePlayerID());
			throw new TerrainCountException(playerHalfMap.getUniquePlayerID(), ETerrain.Water, waterCount);
		}

	}

	@Override
	public void validateState() {
		// TODO Auto-generated method stub

	}

}
