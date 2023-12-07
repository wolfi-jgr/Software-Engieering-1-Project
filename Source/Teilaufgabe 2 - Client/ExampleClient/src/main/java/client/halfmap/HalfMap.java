package client.halfmap;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.enums.EMyTerrain;
import client.positioning.Coordinate;

public class HalfMap{
	
	private Map<Coordinate, EMyTerrain> terrain;
	private Coordinate myCastlePosition;

	private final static Logger logger = LoggerFactory.getLogger(HalfMap.class);

	public HalfMap(HashMap<Coordinate, EMyTerrain> terrain, Coordinate myCastlePosition) {
		if (terrain == null || myCastlePosition == null) {
			logger.error("terrain or myCastlePosition are null in Halfmap");
			throw new IllegalArgumentException("terrain or myCastlePosition are null in Halfmap");
		}

		this.terrain = terrain;
		this.myCastlePosition = myCastlePosition;
	}

	public Coordinate getCastlePosition() {
		return myCastlePosition;
	}

	public Map<Coordinate, EMyTerrain> getTerrain() {
		return terrain;
	}

	public boolean isEmptyOrNull() {
		if(terrain == null || myCastlePosition == null) {
			return true;		
		}
		return terrain.isEmpty() || myCastlePosition.isNotDefined();
	}
}
