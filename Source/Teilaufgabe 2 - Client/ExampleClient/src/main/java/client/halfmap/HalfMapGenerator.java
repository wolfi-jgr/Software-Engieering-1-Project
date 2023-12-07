package client.halfmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.enums.EMyTerrain;
import client.positioning.Coordinate;

public class HalfMapGenerator {

	private final static Logger logger = LoggerFactory.getLogger(HalfMapGenerator.class);

	private List<EMyTerrain> terrainList = new ArrayList<>();
	

	public HashMap<Coordinate, EMyTerrain> generateTerrain() {

		final int totalCount = 50; // 50 fields on my whole HalfMap

		final int grassCount = 24; // 48% from 50 fields
		final int mountainCount = 5; // 10% from 50 fields
		final int waterCount = 7; // 14% from 50 fields

		final int width = 10;
		final int height = 5;

		
		fillTerrainListWithGrassFields(grassCount);
		fillTerrainListWithMountainFields(mountainCount);
		fillTerrainListWithWaterFields(waterCount);

		fillUpToFullField(totalCount - grassCount - mountainCount - waterCount);

		// always random map
		Collections.shuffle(terrainList);

		// converting to Map<Coordinate, Terrain>
		HashMap<Coordinate, EMyTerrain> terrain = convertTerrainListToMap(width, height);

		logger.info("HalfMap is generated.. now Validation.");
		return terrain;
	}

	private HashMap<Coordinate, EMyTerrain> convertTerrainListToMap(int width, int height) {
		HashMap<Coordinate, EMyTerrain> terrain = new HashMap<>();

		int count = 0;

		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				terrain.put(new Coordinate(x, y), terrainList.get(count));
				count++;
			}

		return terrain;
	}

	private void addToTerrainList(EMyTerrain terrain) {
		terrainList.add(terrain);
	}

	private void fillUpToFullField(int remainingCount) { // maybe more grass fields
		Random rand = new Random();

		for (int i = 0; i < remainingCount; ++i) {
			int res = rand.nextInt(100);
			if (res < 60) // change and try for performance

				addToTerrainList(EMyTerrain.MOUNTAIN);
			else
				addToTerrainList(EMyTerrain.WATER);
		}

	}

	private void fillTerrainListWithWaterFields(int waterCount) {
		for (int i = 0; i < waterCount; ++i) {
			addToTerrainList(EMyTerrain.WATER);
		}

	}

	private void fillTerrainListWithMountainFields(int mountainCount) {
		for (int i = 0; i < mountainCount; ++i) {
			addToTerrainList(EMyTerrain.MOUNTAIN);
		}
	}

	private void fillTerrainListWithGrassFields(int grassCount) {
		for (int i = 0; i < grassCount; ++i) {
			addToTerrainList(EMyTerrain.GRASS);
		}
	}

	public HalfMap generateHalfMap() {

		HashMap<Coordinate, EMyTerrain> terrain = generateTerrain();
		Coordinate castle = putCastleOnRandomGrassField(terrain);

		HalfMap hm = new HalfMap(terrain, castle);
		HalfMapValidation hmv = new HalfMapValidation(terrain, castle);
		
		if(!hmv.validateTerrain()) {
			logger.warn("HalfMap was not ok, generating new one..");
			return generateHalfMap();
			
		}else {
			logger.debug("HalfMap was ok.");
			return hm;
		}
	}

	private Coordinate putCastleOnRandomGrassField(Map<Coordinate, EMyTerrain> terrain) {
		
		Random random = new Random();
		Coordinate castle = new Coordinate(random.nextInt(10), random.nextInt(5));

		if (terrain.get(castle) != EMyTerrain.GRASS)
			castle = putCastleOnRandomGrassField(terrain);

		return castle;
	}
}
