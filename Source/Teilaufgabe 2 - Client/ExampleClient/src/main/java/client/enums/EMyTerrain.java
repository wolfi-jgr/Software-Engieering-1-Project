package client.enums;


public enum EMyTerrain {
	GRASS(1), MOUNTAIN(2), WATER(1234);

	private int cost;
	
	EMyTerrain(int cost) {
		this.cost=cost;
	}

	public int getCost() {
		return cost;
	}
}
