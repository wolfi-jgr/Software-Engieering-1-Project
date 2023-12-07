package client.movehandling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.enums.EMyTerrain;
import client.model.GameModel;
import client.positioning.Coordinate;
import client.exceptions.CoordinateOutOfBoundsException;

public class Pathfinder {

	private final static Logger logger = LoggerFactory.getLogger(Pathfinder.class);

	private GameModel gameModel;

	public Pathfinder(GameModel gameModel) {

		this.gameModel = gameModel;
	}
	
	/*
	 * TAKEN FROM <2>
	 * https://dzenanhamzic.com/2016/12/14/dijkstras-algorithm-implementation-in-java/
	 * 
	 * Source was an inspiration for my implementation.
	 * 
	 */
	//TAKEN FROM START <2>
	public Queue<Coordinate> dijkstraFromTo(Coordinate fromCoordinate, Coordinate toCoordinate) throws CoordinateOutOfBoundsException {

		Map<Coordinate, Integer> costs = new HashMap<>();
		PriorityQueue<Coordinate> endpoints = new PriorityQueue<Coordinate>(
				(coord1, coord2) -> Integer.compare(costs.get(coord1), costs.get(coord2)));

		Coordinate newStart = fromCoordinate;
		costs.put(newStart, 0);

		Map<Coordinate, Queue<Coordinate>> path = new HashMap<>();
		path.put(newStart, new LinkedList<>());
		Set<Coordinate> visited = new HashSet<>();

		while (!visited.contains(toCoordinate)) {//if visited once -> shortest path
			Map<Coordinate, EMyTerrain> neighbours = getNeighbours(newStart);

			neighbours.keySet().removeAll(visited);
			visited.add(newStart);

			for (Entry<Coordinate, EMyTerrain> neighbour : neighbours.entrySet()) {

				Coordinate neighbourCoordinate = neighbour.getKey();
				EMyTerrain neighbourTerrain = neighbour.getValue();
				int newCost = costs.get(newStart) + gameModel.getTerrainAt(newStart).getCost()
						+ neighbourTerrain.getCost();

				if (!costs.containsKey(neighbourCoordinate) || newCost < costs.get(neighbourCoordinate)) { 
					//if no costs calculated or less cost 
					costs.put(neighbourCoordinate, newCost);
					Queue<Coordinate> newPath = new LinkedList<>();
					newPath.addAll(path.get(newStart));
					newPath.add(neighbourCoordinate);
					path.put(neighbourCoordinate, newPath);
					endpoints.add(neighbour.getKey());
				}
			}

			newStart = endpoints.poll(); // Poll the node with the lowest cost
		}

		logger.debug("going from: " + fromCoordinate.toString() + " to " + toCoordinate.toString());
		return path.get(toCoordinate);
	}
	
	//TAKEN FROM END <2>

	private Map<Coordinate, EMyTerrain> getNeighbours(Coordinate coord) throws CoordinateOutOfBoundsException {

		Map<Coordinate, EMyTerrain> mapOfNeighbours = new HashMap<>();

		if (hasLeftNeighbour(coord)) {
			Coordinate rightNeighbour = new Coordinate(coord.getX() - 1, coord.getY());
			mapOfNeighbours.put(rightNeighbour, gameModel.getTerrainAt(rightNeighbour));
		}
		if (hasRightNeighbour(coord)) {
			Coordinate leftNeighbour = new Coordinate(coord.getX() + 1, coord.getY());
			mapOfNeighbours.put(leftNeighbour, gameModel.getTerrainAt(leftNeighbour));
		}
		if (hasTopNeighbour(coord)) {
			Coordinate topNeighbour = new Coordinate(coord.getX(), coord.getY() - 1);
			mapOfNeighbours.put(topNeighbour, gameModel.getTerrainAt(topNeighbour));
		}
		if (hasBottomNeighbour(coord)) {
			Coordinate bottomNeighbour = new Coordinate(coord.getX(), coord.getY() + 1);
			mapOfNeighbours.put(bottomNeighbour, gameModel.getTerrainAt(bottomNeighbour));
		}
		return mapOfNeighbours;
	}

	private boolean hasLeftNeighbour(Coordinate coord) {
		return coord.getX() > 0;
	}

	private boolean hasRightNeighbour(Coordinate coord) {
		return coord.getX() < gameModel.getMapWidth() - 1;
	}

	private boolean hasTopNeighbour(Coordinate coord) {
		return coord.getY() > 0;
	}

	private boolean hasBottomNeighbour(Coordinate coord) {
		return coord.getY() < gameModel.getMapHeight() - 1;
	}
}
