package client.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.fullmap.FullMap;
import client.positioning.Coordinate;
import client.enums.EMyGameEntity;
import client.enums.EMyTerrain;
import client.exceptions.CoordinateOutOfBoundsException;

public class GameModel {

	private final static Logger logger = LoggerFactory.getLogger(GameModel.class);

	private boolean collectedTreasure;
	private int roundCounter;

	private FullMap fullMap;

	private PropertyChangeSupport changesForCLI = new PropertyChangeSupport(this);

	public GameModel(FullMap fullMap) {

		if (fullMap.isNull()) {
			logger.error("terrain or entityPositions are null");
			throw new IllegalArgumentException("terrain or entityPositions are null");
		}
		if (fullMap.isEmpty()) {
			logger.error("terrain or entityPositions are empty");
			throw new IllegalArgumentException("terrain or entityPositions are empty");
		}

		this.collectedTreasure = false;

		this.fullMap = fullMap;
		this.roundCounter = 0;
	}

	public GameModel() {
		this.fullMap = new FullMap();

		this.roundCounter = 0;

	}
	
	public void addListener(PropertyChangeListener view) {
		changesForCLI.addPropertyChangeListener(view);
	}

	public void setEntityChanges(Map<EMyGameEntity, Coordinate> currentEntities) {
		if (currentEntities == null) {
			logger.error("entities are null in updateEntities");
			throw new IllegalArgumentException("entities are null in updateEntities");
		}

		if (currentEntities.get(EMyGameEntity.MYPLAYER) == null) {
			logger.error("myplayer is null in updateEntities()");
			throw new IllegalArgumentException("myplayer is null in updateEntities()");
		}

		Map<EMyGameEntity, Coordinate> oldEntities = fullMap.getGameEntities();
		Map<EMyGameEntity, Coordinate> gameEntities = currentEntities;

		fullMap.setGameEntities(gameEntities);
		handleTreasureCollection(oldEntities);
		incrementRoundCounter();
		
		changesForCLI.firePropertyChange("gameEntities", oldEntities, gameEntities);
	}

	private void handleTreasureCollection(Map<EMyGameEntity, Coordinate> entities) {
		if (isTreasureToCollect(entities) && amIOnTreasure()) {
			collectTreasure();
			collectedTreasure = true;
		}
	}

	private boolean amIOnTreasure() {
		if (fullMap.getGameEntities().get(EMyGameEntity.MYPLAYER)
				.equals(fullMap.getGameEntities().get(EMyGameEntity.MYTREASURE))) {
			return true;
		}
		return false;
	}

	private boolean isTreasureToCollect(Map<EMyGameEntity, Coordinate> entities) {
		return entities != null && entities.containsKey(EMyGameEntity.MYTREASURE) && !isTreasureCollected();
	}

	private void incrementRoundCounter() {
		int roundCounterNew = roundCounter + 1;
		changesForCLI.firePropertyChange("roundCounter", roundCounter, roundCounterNew);
		roundCounter++;
	}

	public boolean isTreasureCollected() {
		return collectedTreasure;
	}

	public void collectTreasure() {
		boolean treasureOld = collectedTreasure;
		collectedTreasure = true;
		changesForCLI.firePropertyChange("isTreasureCollected", treasureOld, collectedTreasure);
		logger.info("Treasure has been collected");
	}

	

	public EMyTerrain getTerrainAt(Coordinate coord) throws CoordinateOutOfBoundsException {

		Map<Coordinate, EMyTerrain> terrainMap = fullMap.getTerrain();
		if (coord == null || coord.isNotDefined()) {
			throw new IllegalArgumentException("Coordinate is null or not defined");
		}
		if (terrainMap == null || terrainMap.isEmpty()) {
			throw new IllegalArgumentException("Terrain map is null");
		}
		if (coord.isOutOfBounds(fullMap.getHeight(), fullMap.getWidth())) {
			throw new CoordinateOutOfBoundsException("Coordinate is out of bounds" + coord);
		}
		return terrainMap.get(coord);
	}

	public Coordinate getPositionOfEntity(EMyGameEntity entity) {
		return fullMap.getGameEntities().get(entity);
	}

	public Map<EMyGameEntity, Coordinate> getGameEntities() {
		return new HashMap<>(fullMap.getGameEntities());
	}

	public Map<Coordinate, EMyTerrain> getTerrain() {

		return fullMap.getTerrain();
	}

	public int getMapHeight() {

		return fullMap.getHeight();
	}

	public int getMapWidth() {

		return fullMap.getWidth();
	}

	public boolean isEmpty() {

		return fullMap.isEmpty();
	}

}
