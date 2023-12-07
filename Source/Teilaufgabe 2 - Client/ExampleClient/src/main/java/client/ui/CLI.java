package client.ui;import java.beans.PropertyChangeEvent;import java.beans.PropertyChangeListener;import java.util.ArrayList;import java.util.List;import java.util.Map;import java.util.Map.Entry;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import client.enums.EMyGameEntity;import client.enums.EMyTerrain;import client.halfmap.HalfMap;import client.model.GameModel;import client.positioning.Coordinate;public class CLI implements PropertyChangeListener {	private final static Logger logger = LoggerFactory.getLogger(CLI.class);	private Map<EMyGameEntity, Coordinate> gameEntities;	private GameModel gameModel;	private boolean isTreasureCollected;	private int roundCounter = 0;	public CLI(GameModel gameModel) {		gameModel.addListener(this);		this.gameModel = gameModel;		this.gameEntities = gameModel.getGameEntities();		this.isTreasureCollected = gameModel.isTreasureCollected();	}///////////////////////////////////////	public CLI(HalfMap testm) {			}	public void printMyHalfMap(HalfMap halfMap) {		List<List<String>> toPrint = mapTo2DList(halfMap.getTerrain(), 5, 10, true);		StringBuilder output = new StringBuilder();		for (List<String> row : toPrint) {			for (String field : row) {				output.append(field);			}			output.append('\n');		}		System.out.println(output.toString());	}//////////////////////////////////////////	public void printGame() {		List<List<String>> toPrint = mapTo2DList(gameModel.getTerrain(), gameModel.getMapHeight(),				gameModel.getMapWidth(), false);		StringBuilder output = new StringBuilder();		for (List<String> row : toPrint) {			for (String field : row) {				output.append(field);			}			output.append('\n');		}		output.append(getExtraInfo());		System.out.println(output.toString());	}	protected String getExtraInfo() {		StringBuilder output = new StringBuilder();		output.append("fields:\t#: Grass\n\t_: Water\n\tA: Mountain").append('\n');		output.append(				"special fields:\n\t1: my player\n\t2: enemy player\n\tX: treasure\n\tC: my castle\n\tO: enemyCastle")				.append('\n');		output.append("round: ").append(getRoundCounter()).append('\n');		output.append("treasure collected: ");		output.append(getTreasureCollected());		return output.toString();	}	protected int getRoundCounter() {		return roundCounter;	}	protected boolean getTreasureCollected() {		return isTreasureCollected;	}	public void printLoss() {		System.out.println("Defeat.\n");	}	public void printVictory() {		System.out.println("Victory!\n");	}		/*	 * can be used with HalfMap and FullMap 	 * for debugging or visualizing my generated HalfMap at the start of the game 	 */	private List<List<String>> mapTo2DList(Map<Coordinate, EMyTerrain> terrainOfGivenMap, int height, int width,			boolean halfMapFlag) {		List<List<String>> xyList = new ArrayList<>();		for (int y = 0; y < height; ++y) {			List<String> row = new ArrayList<>();			xyList.add(row);			for (int x = 0; x < width; ++x) {				Coordinate coord = new Coordinate(x, y);				EMyTerrain terrain = terrainOfGivenMap.get(coord);				List<EMyGameEntity> entities = new ArrayList<EMyGameEntity>();				;				if (!halfMapFlag) {					entities = getEntitiesByCoordinate(coord);				}				if (entities.isEmpty()) {					String symbol = getStringForTerrain(terrain);					row.add(symbol);				} else {					String entityString = getEntityString(entities);					row.add(entityString);				}			}		}		return xyList;	}	private List<EMyGameEntity> getEntitiesByCoordinate(Coordinate coord) {		List<EMyGameEntity> entityToReturn = new ArrayList<>();		for (Entry<EMyGameEntity, Coordinate> eachEntry : gameEntities.entrySet()) {			if (eachEntry.getValue().equals(coord)) {				entityToReturn.add(eachEntry.getKey());			}		}		return entityToReturn;	}	private String getEntityString(List<EMyGameEntity> entities) {		if (entities.contains(EMyGameEntity.MYPLAYER)) {			return " 1 ";		}		if (entities.contains(EMyGameEntity.ENEMYPLAYER)) {			return " 2 ";		}		if (entities.contains(EMyGameEntity.MYCASTLE)) {			return " C ";		}		if (entities.contains(EMyGameEntity.ENEMYCASTLE)) {			return " O ";		}		if (entities.contains(EMyGameEntity.MYTREASURE)) {			return " X ";		}		logger.warn("not a known symbol for entity in getEntityString() [CLI]" + entities.toString());		return " ? ";	}	protected String getStringForTerrain(EMyTerrain terrain) {		switch (terrain) {		case GRASS:			return " # ";		case MOUNTAIN:			return " A ";		case WATER:			return " _ ";		default:			return " ? ";		}	}	@SuppressWarnings("unchecked")	@Override	public void propertyChange(PropertyChangeEvent event) {		Object model = event.getSource();		Object newValue = event.getNewValue();		if (!(model instanceof GameModel)) {			throw new RuntimeException("model was not of type FullMap");		}		switch (event.getPropertyName()) {		case "gameEntities":			if (newValue instanceof Map<?, ?>) {				try {					gameEntities = (Map<EMyGameEntity, Coordinate>) newValue;				} catch (IllegalArgumentException e) {					logger.error("gameEntities could not be initialized in propertyChange() [CLI]");					throw e;				}				break;			}		case "isTreasureCollected":			if (newValue instanceof Boolean) {				isTreasureCollected = (Boolean) newValue;				break;			}		case "roundCounter":			if (newValue instanceof Integer) {				roundCounter = (Integer) newValue;				break;			}		default:			logger.error("could not translate to property in [CLI]" + event.getPropertyName());			throw new IllegalArgumentException("could not translate to property in [CLI]" + event.getPropertyName());		}	}}