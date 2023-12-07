package client.fullmap;

import java.util.HashMap;
import java.util.Map;

import client.enums.EMyGameEntity;
import client.enums.EMyTerrain;
import client.positioning.Coordinate;

public class FullMap {
	
	private Map<Coordinate, EMyTerrain> terrain;
	private int height;
	private int width;
	private Map<EMyGameEntity, Coordinate> gameEntities;

	public FullMap(Map<Coordinate, EMyTerrain> terrain, Map<EMyGameEntity, Coordinate> gameEntities) {
		
		this.terrain = terrain;
		this.height = calculateHeight(terrain);
		this.width = calculateWidth(terrain);
		this.gameEntities = gameEntities;
	}

	public FullMap() {
		this.terrain = new HashMap<>();
		this.gameEntities = new HashMap<>();

		this.height = 0;
		this.width = 0;
	}

	private int calculateWidth(Map<Coordinate, EMyTerrain> terrain) {
		int maxWidth = 0;
		for (Coordinate coordinate : terrain.keySet()) {
			if (coordinate.getX() > maxWidth) {
				maxWidth = coordinate.getX();
			}
		}
		return maxWidth + 1;
	}

	private int calculateHeight(Map<Coordinate, EMyTerrain> terrain) {
		int maxHeight = 0;
		for (Coordinate coordinate : terrain.keySet()) {
			if (coordinate.getY() > maxHeight) {
				maxHeight = coordinate.getY();
			}
		}
		return maxHeight + 1;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public Map<Coordinate, EMyTerrain> getTerrain() { // shallow copy
		return new HashMap<>(terrain);
	}

	public Map<EMyGameEntity, Coordinate> getGameEntities() { // shallow copy
		return new HashMap<>(gameEntities);
	}

	public boolean isNull() {
		return terrain == null || gameEntities == null;
	}
	
	public boolean isEmpty() {
		return terrain.isEmpty() || gameEntities.isEmpty();
	}

	public void setGameEntities(Map<EMyGameEntity, Coordinate> gameEntities) {
		this.gameEntities=gameEntities;
		
	}
}
