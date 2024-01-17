package server.exceptions;

public class CastleCountAndOrLocationException extends GenericExampleException {

	public CastleCountAndOrLocationException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
		// TODO Auto-generated constructor stub
	}

	public CastleCountAndOrLocationException(int castleCount, int castleCountOnGrass) {
		super("CastleCountAndOrLocationException", "the castleCount is: " + castleCount
				+ " and should be 1, the castleCountOnGrass is: " + castleCountOnGrass + "but should be 1.");
	}

}
