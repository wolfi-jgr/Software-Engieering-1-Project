package client.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.controller.EMyClientState;
import client.enums.EMyGameEntity;
import client.enums.EMyTerrain;
import client.fullmap.FullMap;
import client.halfmap.HalfMap;
import client.movehandling.EMyMove;
import client.positioning.Coordinate;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.PlayerState;

public class NetworkConverter {
	private final static Logger logger = LoggerFactory.getLogger(NetworkConverter.class);

	protected List<PlayerHalfMapNode> myHalfMapToNetworkHalfMap(HalfMap halfmap) {

		List<PlayerHalfMapNode> nodes = new ArrayList<>();
		Coordinate castle = halfmap.getCastlePosition();
		boolean isCastle = false;

		for (Entry<Coordinate, EMyTerrain> entry : halfmap.getTerrain().entrySet()) {
			if (entry.getKey().equals(castle)) {
				isCastle = true;
			}
			PlayerHalfMapNode tempNode = new PlayerHalfMapNode(entry.getKey().getX(), entry.getKey().getY(), isCastle,
					toNetworkTerrain(entry.getValue()));
			isCastle = false;
			nodes.add(tempNode);
		}
		return nodes;
	}

	protected FullMap networkMapToMyFullMap(Collection<FullMapNode> networkNodes) {
		Map<Coordinate, EMyTerrain> terrain = new HashMap<>();
		Map<EMyGameEntity, Coordinate> gameEntities = new HashMap<>();
		FullMap myFullMap = new FullMap();

		for (FullMapNode eachNode : networkNodes) {
			Coordinate coord = new Coordinate(eachNode.getX(), eachNode.getY());
			EMyTerrain eachTerrain = toMyTerrain(eachNode.getTerrain());

			terrain.put(coord, eachTerrain);
			gameEntities.putAll(getGameEntitiesFromNetworkNode(eachNode, coord));
		}
		myFullMap = new FullMap(terrain, gameEntities);

		if (myFullMap.isEmpty()) {

			logger.error("FullMap is not ok after converting..");
			throw new RuntimeException("FullMap is not ok after converting..");
		}

		return myFullMap;
	}

	

	protected Map<EMyGameEntity, Coordinate> getGameEntitiesFromNetworkNode(FullMapNode node , Coordinate coord) {
		Map<EMyGameEntity, Coordinate> entities = new HashMap<EMyGameEntity, Coordinate>();

		EFortState fortState = node.getFortState();
		if (fortState == EFortState.EnemyFortPresent) {
			entities.put(EMyGameEntity.ENEMYCASTLE, coord);
		}
		if (fortState == EFortState.MyFortPresent) {
			entities.put(EMyGameEntity.MYCASTLE,coord);
		}

		EPlayerPositionState playerPositionState = node.getPlayerPositionState();
		if (playerPositionState.representsMyPlayer()) {
			entities.put(EMyGameEntity.MYPLAYER,coord);
		}
		if (playerPositionState == EPlayerPositionState.EnemyPlayerPosition) {
			entities.put(EMyGameEntity.ENEMYPLAYER,coord);
		}
		
		ETreasureState treasureState = node.getTreasureState();
		if (treasureState == ETreasureState.MyTreasureIsPresent) {
			entities.put(EMyGameEntity.MYTREASURE,coord);
		}
		return entities;
	}

	private EMyTerrain toMyTerrain(ETerrain terrain) {
		switch (terrain) {
		case Grass:
			return EMyTerrain.GRASS;
		case Water:
			return EMyTerrain.WATER;
		case Mountain:
			return EMyTerrain.MOUNTAIN;
		default:
			return null;
		}
	}

	protected ETerrain toNetworkTerrain(EMyTerrain terrain) {
		switch (terrain) {
		case GRASS:
			return ETerrain.Grass;
		case WATER:
			return ETerrain.Water;
		case MOUNTAIN:
			return ETerrain.Mountain;
		default:
			return null;
		}
	}

	protected EMove myMovetoNetworkMove(EMyMove move) {

		switch (move) {
		case UP:
			return EMove.Up;
		case DOWN:
			return EMove.Down;
		case LEFT:
			return EMove.Left;
		case RIGHT:
			return EMove.Right;
		default:
			return null;
		}
	}

	protected EMyClientState toMyPlayerState(PlayerState state) {
		switch (state.getState()) {
		case Lost:
			return EMyClientState.LOST;
		case Won:
			return EMyClientState.WON;
		case MustAct:
			return EMyClientState.MUSTACT;
		case MustWait:
			return EMyClientState.MUSTWAIT;
		default:
			return null;
		}
	}

}
