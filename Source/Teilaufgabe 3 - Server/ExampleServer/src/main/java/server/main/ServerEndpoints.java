package server.main;

import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import messagesbase.ResponseEnvelope;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ERequestState;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import server.eachgame.Game;
import server.exceptions.GenericExampleException;
import server.gamescontroller.Gamemaster;

@RestController
@RequestMapping(value = "/games")
public class ServerEndpoints {


	private Gamemaster controller = new Gamemaster();

	// ADDITIONAL TIPS ON THIS MATTER ARE GIVEN THROUGHOUT THE TUTORIAL SESSION!
	// Note, the same network messages which you have used for the Client (along
	// with its documentation) apply to the Server too.

	/*
	 * Please do NOT add all the necessary code in the methods provided below. When
	 * following the single responsibility principle, those methods should only
	 * contain the bare minimum related to network handling. Such as the converts
	 * which convert the objects from/to internal data objects to/from messages.
	 * Include the other logic (e.g., new game creation and game id handling) by
	 * means of composition (i.e., other classes should provide it).
	 */

	// below you can find two example endpoints (i.e., one GET and one POST based
	// endpoint, which are all endpoint types that you need),
	// Hence, all the other endpoints can be defined similarly.

	// example for a GET endpoint based on /games
	// similar to the client, the HTTP method and the expected data types are
	// specified at the server-side too
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody UniqueGameIdentifier newGame(
			@RequestParam(required = false, defaultValue = "false", value = "enableDebugMode") boolean enableDebugMode,
			@RequestParam(required = false, defaultValue = "false", value = "enableDummyCompetition") boolean enableDummyCompetition) {

		// set showExceptionHandling to true to test/play around with the automatic
		// exception handling (see the handleException method at the bottom)
		// this is just some testing code that you can see how exceptions can be used to
		// signal errors to the client, you can REMOVE
		// these lines in your actual server implementation
		boolean showExceptionHandling = false;
		if (showExceptionHandling) {
			// if any error occurs, simply throw an exception with inherits from
			// GenericExampleException
			// the given code than takes care of responding with an error message to the
			// client based on the @ExceptionHandler below
			// make yourself familiar with this concept by setting
			// showExceptionHandling=true
			// and creating a new game through the browser
			// your implementation should use more useful error messages and specialized
			// exception classes
			throw new GenericExampleException("Name: Something", "Message: went totally wrong");
		}

		// TIP: you will need to adapt this part to generate a game id with the valid
		// length. A simple solution for this
		// would be creating an alphabet and choosing random characters from it till the
		// new game id becomes long enough

		Game currentGame = controller.createNewGame();

		UniqueGameIdentifier gameIdentifier = new UniqueGameIdentifier(currentGame.getGameID());
		return gameIdentifier;

		// you will need to include additional logic, e.g., additional classes
		// which create, store, validate, etc. exchanged data
	}

	// example for a POST endpoint based on /games/{gameID}/players
	@RequestMapping(value = "/{gameID}/players", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<UniquePlayerIdentifier> registerPlayer(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerRegistration playerRegistration) {

		controller.handlePlayersRequest(gameID);
		
		UniquePlayerIdentifier newPlayerID = new UniquePlayerIdentifier(UUID.randomUUID().toString());
		
		controller.addPlayer(gameID, newPlayerID.getUniquePlayerID(), playerRegistration);
		
		ResponseEnvelope<UniquePlayerIdentifier> playersResponse = new ResponseEnvelope<>(newPlayerID);
		return playersResponse;

	}

	@RequestMapping(value = "/{gameID}/halfmaps", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<ERequestState> getHalfMap(
			@Validated @PathVariable UniqueGameIdentifier gameID, @Validated @RequestBody PlayerHalfMap playerHalfMap) {
		
		controller.handleHalfMapRequest(gameID, playerHalfMap);
		ResponseEnvelope<ERequestState> halfMapResponse = new ResponseEnvelope<>(ERequestState.Okay);
		return halfMapResponse;

	}

	@RequestMapping(value = "/{gameID}/states/{playerID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<GameState> getGameState(@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @PathVariable UniquePlayerIdentifier playerID) {
		
		
		GameState gameState = new GameState();
		gameState = controller.handleStateRequest(gameID, playerID.getUniquePlayerID());

		ResponseEnvelope<GameState> gameStateResponse = new ResponseEnvelope<>(gameState);
		return gameStateResponse;

	}

	@ExceptionHandler({ GenericExampleException.class })
	public @ResponseBody ResponseEnvelope<?> handleException(GenericExampleException ex, HttpServletResponse response) {
		ResponseEnvelope<?> result = new ResponseEnvelope<>(ex.getErrorName(), ex.getMessage());

		// reply with 200 OK as defined in the network documentation
		// Side note: We only do this here for simplicity reasons. For future projects,
		// you should check out HTTP status codes and
		// what they can be used for. Note, the WebClient used during the Client
		// implementation can react
		// to them using the .onStatus(...) method.
		response.setStatus(HttpServletResponse.SC_OK);
		return result;
	}
}
