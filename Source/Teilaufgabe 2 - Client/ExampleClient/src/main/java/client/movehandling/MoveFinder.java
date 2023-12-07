package client.movehandling;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.positioning.Coordinate;
import client.model.GameModel;
import client.enums.EMyGameEntity;
import client.enums.EMyTerrain;
import client.exceptions.CoordinateOutOfBoundsException;
import client.exceptions.IllegalMoveException;

public class MoveFinder {
	private final static Logger logger = LoggerFactory.getLogger(MoveFinder.class);
	private GameModel gameModel;

	private Coordinate myHalfTopLeftCoordinate;
	private Coordinate myHalfBottomRightCoordinate;
	private Coordinate enemyHalfTopLeft;
	private Coordinate enemyHalfBottomRight;

	private Queue<EMyMove> moves;

	private Queue<Coordinate> newPath = null;
	private EMyGameEntity target;

	private Queue<Coordinate> toVisit;
	private Set<Coordinate> visited;

	public MoveFinder(GameModel gameModel) throws CoordinateOutOfBoundsException {

		this.target = EMyGameEntity.MYTREASURE;
		this.gameModel = gameModel;
		setHalfMapOrientation();
		this.toVisit = setToQueue(getGrassCoordinatesOnHalf(myHalfTopLeftCoordinate, myHalfBottomRightCoordinate));
		this.visited = new HashSet<Coordinate>();
		this.moves = new LinkedList<>();

	}

	public EMyMove getNextMove() throws Exception {

		Coordinate myCoord = getMyPosition();

		if (gameModel.getTerrainAt(myCoord) == EMyTerrain.MOUNTAIN) {
			addPlusNeighboursToVisited(myCoord);
			addXNeighboursToVisited(myCoord);
		}

		toVisit.removeAll(visited);

		if (isMovesEmptyOrNull()) {
			if (isCastleHuntTriggered()) {
				switchToCastleHuntingStrategy();
			}
			moves = moveOneField(getNextCoordinateOfPath());
		}
		return moves.remove();
	}

	private Queue<EMyMove> moveOneField(Coordinate toCoordinate) throws Exception {
		if (gameModel == null || gameModel.isEmpty()) {
			logger.error("fullMap is null or empty in moveOneField() [MoveFinder]");
			throw new IllegalArgumentException("fullMap is null or empty in moveOneField() [MoveFinder]");
		}

		if (toCoordinate == null || toCoordinate.isOutOfBounds(gameModel.getMapHeight(), gameModel.getMapWidth())) {
			logger.error("Coordinate is out of bounds in moveOneField() [Movefinder]" + toCoordinate.toString());
			throw new IllegalArgumentException(
					"Coordinate is out of bounds in moveOneField() [Movefinder]" + toCoordinate.toString());
		}

		Coordinate myCoordinate = gameModel.getPositionOfEntity(EMyGameEntity.MYPLAYER); 

		EMyMove dir = validDirection(myCoordinate, toCoordinate);

		Queue<EMyMove> moves = new LinkedList<>();
		int toLeave = gameModel.getTerrainAt(myCoordinate).getCost();
		int toEnter = gameModel.getTerrainAt(toCoordinate).getCost();

		moves.addAll(Collections.nCopies(toLeave + toEnter, dir));

		return moves;
	}

	private EMyMove validDirection(Coordinate start, Coordinate end) throws IllegalMoveException {
		if (wantsToMoveUp(start, end)) {
			return EMyMove.UP;
		}
		if (wantsToMoveDown(start, end)) {
			return EMyMove.DOWN;
		}
		if (wantsToMoveRight(start, end)) {
			return EMyMove.RIGHT;
		}
		if (wantsToMoveLeft(start, end)) {
			return EMyMove.LEFT;
		}

		logger.error("my move is not in a valid direction because it went from" + start.toString() + " to "
				+ end.toString());
		throw new IllegalMoveException(start, end);

	}

	private boolean wantsToMoveUp(Coordinate start, Coordinate end) {
		return (start.getY() - end.getY() == 1) && !wantsToMoveDown(start, end) && !wantsToMoveLeft(start, end)
				&& !wantsToMoveRight(start, end);
	}

	private boolean wantsToMoveDown(Coordinate start, Coordinate end) {
		return (start.getY() - end.getY() == -1) && !wantsToMoveUp(start, end) && !wantsToMoveLeft(start, end)
				&& !wantsToMoveRight(start, end);
	}

	private boolean wantsToMoveRight(Coordinate start, Coordinate end) {
		return (start.getX() - end.getX() == -1) && !wantsToMoveDown(start, end) && !wantsToMoveLeft(start, end)
				&& !wantsToMoveUp(start, end);
	}

	private boolean wantsToMoveLeft(Coordinate start, Coordinate end) {
		return (start.getX() - end.getX() == 1) && !wantsToMoveDown(start, end) && !wantsToMoveUp(start, end)
				&& !wantsToMoveRight(start, end);
	}

	private boolean isCastleHuntTriggered() {
		return gameModel.isTreasureCollected() && target != EMyGameEntity.ENEMYCASTLE;
	}

	private void switchToCastleHuntingStrategy() throws CoordinateOutOfBoundsException {
		target = EMyGameEntity.ENEMYCASTLE;
		toVisit = setToQueue(getGrassCoordinatesOnHalf(enemyHalfTopLeft, enemyHalfBottomRight));
		logger.debug("Switching to castle hunting strategy");
	}

	private boolean isMovesEmptyOrNull() {
		return moves == null || moves.isEmpty();
	}

	private Queue<Coordinate> setToQueue(Set<Coordinate> setToConvert) {
		Queue<Coordinate> res = new LinkedList<>();
		res.addAll(setToConvert);
		return res;
	}

	private Coordinate getNextCoordinateOfPath() throws Exception {
		Pathfinder pathfinder = new Pathfinder(this.gameModel);

		if (shouldPathToTreasure()) {
			newPath = pathfinder.dijkstraFromTo(getMyPosition(), getTreasurePosition());
		}
		if (shouldPathToEnemyCastle()) {
			newPath = pathfinder.dijkstraFromTo(getMyPosition(), getEnemyCastlePosition());
		}
		if (isNewPathNullOrEmpty()) {
			handleEmptyPath(pathfinder);
		}

		Coordinate nextCoordinate = newPath.remove();
		visited.add(nextCoordinate);

		return nextCoordinate;
	}

	private Coordinate getEnemyCastlePosition() {
		return gameModel.getPositionOfEntity(EMyGameEntity.ENEMYCASTLE);
	}

	private Coordinate getTreasurePosition() {
		return gameModel.getPositionOfEntity(EMyGameEntity.MYTREASURE);
	}

	private Coordinate getMyPosition() {
		return gameModel.getPositionOfEntity(EMyGameEntity.MYPLAYER);
	}

	private boolean isNewPathNullOrEmpty() {
		return newPath == null || newPath.isEmpty();
	}

	private boolean shouldPathToTreasure() {
		return !gameModel.isTreasureCollected() && gameModel.getPositionOfEntity(EMyGameEntity.MYTREASURE) != null;
	}

	private boolean shouldPathToEnemyCastle() {
		return gameModel.isTreasureCollected() && target == EMyGameEntity.ENEMYCASTLE
				&& gameModel.getPositionOfEntity(EMyGameEntity.ENEMYCASTLE) != null;
	}

	private void handleEmptyPath(Pathfinder pathfinder) throws CoordinateOutOfBoundsException {
		newPath = pathfinder.dijkstraFromTo(gameModel.getPositionOfEntity(EMyGameEntity.MYPLAYER), toVisit.remove());
	}

	private void addXNeighboursToVisited(Coordinate myCoord) {// diagonal neighbours to visited
		for (Coordinate entry : getXNeighbours(myCoord)) {
			visited.add(entry);
		}
	}

	private void addPlusNeighboursToVisited(Coordinate myCoord) {
		// neighbours above, below, right and left added to
		// visited
		for (Coordinate entry : getPlusNeighbours(myCoord)) {
			visited.add(entry);
		}
	}

	private Set<Coordinate> getPlusNeighbours(Coordinate coord) {

		Set<Coordinate> plusNeighbours = new HashSet<>();

		if (coord.getX() > 0) {
			plusNeighbours.add(new Coordinate(coord.getX() - 1, coord.getY())); // left
		}
		if (coord.getX() < gameModel.getMapWidth() - 1) {
			plusNeighbours.add(new Coordinate(coord.getX() + 1, coord.getY())); // right
		}
		if (coord.getY() > 0) {
			plusNeighbours.add(new Coordinate(coord.getX(), coord.getY() - 1)); // top
		}
		if (coord.getY() < gameModel.getMapHeight() - 1) {
			plusNeighbours.add(new Coordinate(coord.getX(), coord.getY() + 1)); // bottom
		}
		return plusNeighbours;
	}

	private Set<Coordinate> getXNeighbours(Coordinate coord) {

		Set<Coordinate> diagonalNeighbours = new HashSet<>();

		if (coord.getX() > 0 && coord.getY() > 0) {
			diagonalNeighbours.add(new Coordinate(coord.getX() - 1, coord.getY() - 1)); // top left
		}

		if (coord.getY() < gameModel.getMapHeight() - 1 && coord.getX() < gameModel.getMapWidth() - 1) {
			diagonalNeighbours.add(new Coordinate(coord.getX() + 1, coord.getY() + 1)); // bottom right
		}

		if (coord.getX() < gameModel.getMapWidth() - 1 && coord.getY() > 0) {
			diagonalNeighbours.add(new Coordinate(coord.getX() + 1, coord.getY() - 1)); // top right
		}

		if (coord.getX() > 0 && coord.getY() > gameModel.getMapHeight() - 1) {
			diagonalNeighbours.add(new Coordinate(coord.getX() - 1, coord.getY() + 1)); // bottom left
		}

		return diagonalNeighbours;
	}

	private Set<Coordinate> getGrassCoordinatesOnHalf(Coordinate topLeft, Coordinate bottomRight)
			throws CoordinateOutOfBoundsException {
		Set<Coordinate> grassCoordinates = new HashSet<>();

		Coordinate currentCoordinate = new Coordinate();

		for (int x = topLeft.getX(); x <= bottomRight.getX(); x++) {
			for (int y = topLeft.getY(); y <= bottomRight.getY(); y++) {
				currentCoordinate = new Coordinate(x, y);
				if (isGrassFieldAndNotMyCastle(currentCoordinate)) {
	                grassCoordinates.add(currentCoordinate);
	            }
			}
		}

		return grassCoordinates;
	}

	private boolean isGrassFieldAndNotMyCastle(Coordinate currentCoordinate) throws CoordinateOutOfBoundsException {
		 return gameModel.getTerrainAt(currentCoordinate) == EMyTerrain.GRASS
		            && !currentCoordinate.equals(gameModel.getPositionOfEntity(EMyGameEntity.MYCASTLE));
	}

	private void setHalfMapOrientation() {

		if (gameModel.getMapHeight() == 5) { // square map
			int halfWidth = gameModel.getMapWidth() / 2;

			if (gameModel.getPositionOfEntity(EMyGameEntity.MYPLAYER).getX() < halfWidth) {
				// my side is left
				this.myHalfTopLeftCoordinate = new Coordinate(0, 0);
				this.myHalfBottomRightCoordinate = new Coordinate(9, 4);
				this.enemyHalfTopLeft = new Coordinate(5, 0);
				this.enemyHalfBottomRight = new Coordinate(19, 4);
			}

			else {
				// my side is right
				this.enemyHalfTopLeft = new Coordinate(0, 0);
				this.enemyHalfBottomRight = new Coordinate(9, 4);
				this.myHalfTopLeftCoordinate = new Coordinate(5, 0);
				this.myHalfBottomRightCoordinate = new Coordinate(19, 4);
			}
		}

		else { // long map
			int halfHeight = gameModel.getMapHeight() / 2;

			if (gameModel.getPositionOfEntity(EMyGameEntity.MYPLAYER).getY() < halfHeight) {
				// my side is left
				this.myHalfTopLeftCoordinate = new Coordinate(0, 0);
				this.myHalfBottomRightCoordinate = new Coordinate(9, 4);
				this.enemyHalfTopLeft = new Coordinate(0, 5);
				this.enemyHalfBottomRight = new Coordinate(9, 9);
			} else {
				// my side is right
				this.enemyHalfTopLeft = new Coordinate(0, 0);
				this.enemyHalfBottomRight = new Coordinate(9, 4);
				this.myHalfTopLeftCoordinate = new Coordinate(0, 5);
				this.myHalfBottomRightCoordinate = new Coordinate(9, 9);
			}
		}
	}
}