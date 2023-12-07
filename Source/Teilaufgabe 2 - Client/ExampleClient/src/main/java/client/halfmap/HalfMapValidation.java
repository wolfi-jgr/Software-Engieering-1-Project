package client.halfmap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.enums.EMyTerrain;
import client.positioning.Coordinate;

public class HalfMapValidation {

	private Map<Coordinate, EMyTerrain> terrain = new HashMap<>();
	private final int width = 10;
	private final int height = 5;
	private Coordinate myCastlePosition = new Coordinate();

	private int floodCount = 0;

	private final static Logger logger = LoggerFactory.getLogger(HalfMapValidation.class);

	public HalfMapValidation(Map<Coordinate, EMyTerrain> halfMap, Coordinate myCastlePosition) {
		if (halfMap.isEmpty()) {
			logger.error("The passed map is empty");
			throw new IllegalArgumentException("The passed map is empty");
		}
		this.terrain = halfMap;
		this.myCastlePosition = myCastlePosition;
	}

	public boolean validateTerrain() {
		return validateTerrainCount() && validateCoordinateOutOfBounds() && validateTerrainCountGoal()
				&& validateWaterCountOfBorders() && validateIslands() && validateCastlePosition();
	}

	private boolean validateTerrainCount() {

		if (terrain.size() == width * height) {

			return true;
		}
		logger.error("HalfMap has not 50 Terrains");
		return false;
	}

	private boolean validateCoordinateOutOfBounds() {
		for (Coordinate coord : terrain.keySet()) {
			if (coord.isOutOfBounds(height, width)) {
				logger.warn("coordinate: " + coord.toString() + " is out of bounds.");
				return false;
			}
		}
		return true;
	}

	private boolean validateTerrainCountGoal() {

		Set<Entry<Coordinate, EMyTerrain>> terrains = terrain.entrySet();

		int countGrass = 0;
		int countMountain = 0;
		int countWater = 0;

		int grassGoal = 24;
		int mountainGoal = 5;
		int waterGoal = 7;

		for (Entry<Coordinate, EMyTerrain> t : terrains) {
			switch (t.getValue()) {
			case GRASS:
				countGrass++;
				break;
			case MOUNTAIN:
				countMountain++;
				break;
			case WATER:
				countWater++;
				break;
			default:
				logger.error("invalid Terrain type.");
				break;
			}
		}

		if (countGrass < grassGoal) {
			logger.warn("less grass Terrains on HalfMap than expected");
			return false;
		}

		if (countMountain < mountainGoal) {
			logger.warn("less mountain Terrains on HalfMap than expected");
			return false;
		}
		if (countWater < waterGoal) {
			logger.warn("less water Terrains on HalfMap than expected");
			return false;
		}

		return true;
	}

	private boolean validateWaterCountOfBorders() {

		Set<Entry<Coordinate, EMyTerrain>> mapEntrySet = terrain.entrySet();

		int countWaterTopBorder = 0;
		int countWaterBottomBorder = 0;
		int countWaterRightBorder = 0;
		int countWaterLeftBorder = 0;

		int waterCountLimit = 4;

		for (Entry<Coordinate, EMyTerrain> eachField : mapEntrySet) {
			if (eachField.getKey().getX() == 0 && eachField.getValue() == EMyTerrain.WATER) {
				countWaterLeftBorder++;
			}
			if (eachField.getKey().getY() == 0 && eachField.getValue() == EMyTerrain.WATER) {
				countWaterTopBorder++;
			}
			if (eachField.getKey().getX() == width - 1 && eachField.getValue() == EMyTerrain.WATER) {
				countWaterRightBorder++;
			}
			if (eachField.getKey().getY() == height - 1 && eachField.getValue() == EMyTerrain.WATER) {
				countWaterBottomBorder++;
			}
		}

		if (countWaterBottomBorder > waterCountLimit || countWaterTopBorder > waterCountLimit
				|| countWaterRightBorder > waterCountLimit / 2 || countWaterLeftBorder > waterCountLimit / 2) {
			logger.warn("too much water terrains on borders");
			return false;
		}
		return true;
	}

	private boolean validateIslands() {

		Map<Coordinate, EMyTerrain> terrainToCheck = new HashMap<Coordinate, EMyTerrain>();
		terrainToCheck.putAll(terrain);

		floodfill(terrainToCheck, new Coordinate(0, 0), new HashSet<Coordinate>());

		int waterCount = 0;
		for (Entry<Coordinate, EMyTerrain> entry : terrainToCheck.entrySet()) {
			if (entry.getValue() == EMyTerrain.WATER) {
				waterCount++;
			}

		}
		if (waterCount + getFloodCount() != width * height) {
			logger.error("HalfMap has an island");
			return false;
		}

		return true;
	}

	private int getFloodCount() {
		return floodCount;
	}
	
	private boolean validateCastlePosition() {
		return terrain.get(myCastlePosition) == EMyTerrain.GRASS;
	}

	private void incrementFloodCount() {
		floodCount++;
	}

	/*
	 * TAKEN FROM <1>
	 * https://www.geeksforgeeks.org/flood-fill-algorithm-implement-fill-paint/
	 * 
	 * 
	 * 
	 * 
	 */

	private void floodfill(Map<Coordinate, EMyTerrain> myHalfMap, Coordinate start, Set<Coordinate> visited) {
		// TAKEN FROM START <1>
		if (start.getX() < 0 || start.getX() >= width || start.getY() < 0 || start.getY() >= height) {
			return;
		}
		if (myHalfMap.get(start) == EMyTerrain.WATER || myHalfMap.get(start) == null) {
			return;
		}
		if (!myHalfMap.containsKey(start)) {
			return;
		}
		if (visited.contains(start)) {
			return;
		}

		incrementFloodCount();
		visited.add(start);
		floodfill(myHalfMap, new Coordinate(start.getX(), start.getY() - 1), visited);

		floodfill(myHalfMap, new Coordinate(start.getX(), start.getY() + 1), visited);

		floodfill(myHalfMap, new Coordinate(start.getX() - 1, start.getY()), visited);

		floodfill(myHalfMap, new Coordinate(start.getX() + 1, start.getY()), visited);
		// TAKEN FROM START <1>
	}

}