package client.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.fullmap.FullMap;
import client.halfmap.HalfMap;
import client.halfmap.HalfMapGenerator;
import client.model.GameModel;
import client.movehandling.EMyMove;
import client.movehandling.MoveFinder;
import client.network.ClientNetwork;
import client.ui.CLI;

public class GameLogic {

	private ClientNetwork network;
	private GameModel gameModel;

	private final static Logger logger = LoggerFactory.getLogger(GameLogic.class);

	public GameLogic(String serverBaseUrl, String gameId) {

		this.network = new ClientNetwork(serverBaseUrl, gameId);

	}

	public void initializeGame() throws Exception {

		// register player
		logger.debug("trying to register player");
		Thread.sleep(400);
		network.registerClient();
		logger.debug("successfully registered my player");
		logger.debug("waiting for other player");
		network.waitForStep();

		logger.debug("both players registered, now creating HalfMap");

		HalfMapGenerator hmg = new HalfMapGenerator();
		HalfMap myHalfMap = hmg.generateHalfMap();

		CLI testUI = new CLI(myHalfMap);
		testUI.printMyHalfMap(myHalfMap);

		logger.debug("HalfMap was generated,now sending to server");
		network.waitForStep();
		network.sendHalfMap(myHalfMap);

		logger.debug("HalfMap was sent to the server, now waiting for other player and FullMap");
		network.waitForStep();
		// getting FullMap from server
		logger.debug("Receiving FullMap from server");
		FullMap fullMap = network.getMyFullMap();
		this.gameModel = new GameModel(fullMap);
		logger.debug("Received FullMap from server successfully");

	}

	public void runGame() {

		try {

			// printing FullMap
			logger.debug("Creating CLI and printing FullMap");
			CLI userInterface = new CLI(gameModel);
			userInterface.printGame();

			// FancyCLI fancyUserInterface = new FancyCLI(gameModel);

			// creating MoveFinder with FullMap
			MoveFinder moveFinder = new MoveFinder(gameModel);
			EMyClientState stateOfMyGame = EMyClientState.MUSTWAIT;
			boolean treasureFlag = false;

			while (true) {

				network.waitForStep();

				// prepare move
				gameModel.setEntityChanges(network.getEntities());

				EMyMove move = moveFinder.getNextMove();

				stateOfMyGame = network.getMyCurrentGameState();
				logger.debug("my GameState in switch: " + stateOfMyGame);

				switch (stateOfMyGame) {
				case MUSTACT:
					network.sendMove(move);
					userInterface.printGame();
					// fancyUserInterface.printGame();
					if (!treasureFlag) {
						if (network.treasureIsCollected()) {
							gameModel.collectTreasure();
							treasureFlag = true;
						}
					}
					break;
				case WON:
					userInterface.printGame();
					userInterface.printVictory();
					System.exit(0);
					break;
				case LOST:
					userInterface.printGame();
					userInterface.printLoss();
					System.exit(0);
					break;
				default:
					logger.error("no case in switch: " + network.getMyCurrentGameState().toString());
					System.exit(0);
				}
			}

		} catch (Exception e) {
			System.out.println("" + e.getMessage());
			logger.error("error in controller (runGame())" + e.getMessage());
			System.exit(1);
		}
	}

}
