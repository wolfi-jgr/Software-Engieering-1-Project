package client.ui;

import client.enums.EMyTerrain;
import client.model.GameModel;

public class FancyCLI extends CLI {

	public FancyCLI(GameModel gameModel) {
		super(gameModel);
	}
	
	
	
	@Override
	public void printGame() {
		super.printGame();
	}
	
	@Override
	protected String getStringForTerrain(EMyTerrain terrain) {
		switch (terrain) {
		case GRASS:
			return " \uD83C\uDF3B ";
		case MOUNTAIN:
			return " \u26F0 ";
		case WATER:
			return " \uD83C\uDF0A ";
		default:
			return " ? ";
		}
	}
	
	@Override
	protected String getExtraInfo() {
		StringBuilder output = new StringBuilder();
		output.append("fields:\t\uD83C\uDF3B: Grass\n\t\uD83C\uDF0A: Water\n\t\u26F0: Mountain").append('\n');
		output.append("special fields:\n\t1: my player\n\t2: enemy player\n\tX: treasure\n\tC: my castle\n\tO: enemyCastle").append('\n');
		output.append("round: ").append(getRoundCounter()).append('\n');
		output.append("treasure collected: ");
		output.append(getTreasureCollected());
		
	return output.toString();
	}

}
