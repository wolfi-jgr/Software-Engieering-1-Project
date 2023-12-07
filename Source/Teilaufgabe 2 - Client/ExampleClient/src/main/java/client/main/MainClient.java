package client.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.controller.GameLogic;
import client.exceptions.CouldNotHandleArgumentsException;

public class MainClient {
	private final static Logger logger = LoggerFactory.getLogger(MainClient.class);

	public static void main(String[] args) throws CouldNotHandleArgumentsException {

		String serverBaseUrl = null;
		String gameID = null;

		if (args.length != 0) {// starting with parameters
			logger.debug("Starting client with parameters");

			try {
				serverBaseUrl = args[1];
				gameID = args[2];
			} catch (Exception e) {
				logger.error("Something went wrong with the given arguments" + e.getMessage());
				throw new CouldNotHandleArgumentsException(serverBaseUrl, gameID);
			}

		} else {// starting in IDE
			logger.debug("Starting client with gameID (dummy)");
			serverBaseUrl = "http://swe1.wst.univie.ac.at:18235";
			gameID = "4rrx7"; // fill in the gameId;
			logger.debug("Starting client with gameID : " + gameID + " (dummy)");
		}
		// creating instance of GameLogic (Controller)
		GameLogic controller = new GameLogic(serverBaseUrl, gameID);
		try {
			controller.initializeGame();
		} catch (Exception e) {
			e.printStackTrace();
		}
		controller.runGame(); // starting the game..
	}
}