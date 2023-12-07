package client.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import client.enums.EMyGameEntity;
import client.fullmap.FullMap;
import client.halfmap.HalfMap;
import client.movehandling.EMyMove;
import client.positioning.Coordinate;
import messagesbase.ResponseEnvelope;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.ERequestState;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerMove;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import reactor.core.publisher.Mono;

public class ClientNetwork {

	private String gameID;
	private UniquePlayerIdentifier uniquePlayerID;
	private WebClient baseWebClient;
	private NetworkConverter netConverter;

	private final static Logger logger = LoggerFactory.getLogger(ClientNetwork.class);

	public client.controller.EMyClientState getMyCurrentGameState() throws Exception {
		return netConverter.toMyPlayerState(getMyPlayerState());
	}

	public ClientNetwork(String serverBaseUrl, String gameId) {

		if (serverBaseUrl == null || gameId == null) {
			logger.error("serverBaseUrl or gameId is null");
			throw new IllegalArgumentException("serverBaseUrl or gameId is null");
		}

		this.gameID = gameId;
		this.netConverter = new NetworkConverter();

		this.baseWebClient = WebClient.builder().baseUrl(serverBaseUrl + "/games")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE).build();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void registerClient() throws Exception {

		PlayerRegistration register = new PlayerRegistration("Wolfgang", "Jäger", "jaegerw98");
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + gameID + "/players")
				.body(BodyInserters.fromValue(register)).retrieve().bodyToMono(ResponseEnvelope.class);

		ResponseEnvelope<UniquePlayerIdentifier> response = webAccess.block();

		if (response.getState() == ERequestState.Error) {
			logger.error("Error state in registerClient(): " + response.getExceptionMessage());
		} else {
			this.uniquePlayerID = response.getData().get();
			logger.info("My PlayerID is: " + uniquePlayerID.getUniquePlayerID());
		}

	}

	public PlayerState getMyPlayerState() throws Exception {

		Set<PlayerState> players = getGameState(uniquePlayerID).getPlayers();

		PlayerState myPlayerDetails = null;

		for (PlayerState p : players) {
			if ((p.getUniquePlayerID()).equals(uniquePlayerID.getUniquePlayerID()))
				myPlayerDetails = p;
		}

		return myPlayerDetails;
	}

	@SuppressWarnings("rawtypes")
	public GameState getGameState(UniquePlayerIdentifier myPlayerID) throws Exception {// NetworkCommunicationException
		if (myPlayerID == null) {
			logger.error("playerId is null in getGameState");
			throw new IllegalArgumentException("playerId is null in getGameState");
		}
		Thread.sleep(400);

		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.GET)
				.uri("/" + gameID + "/states/" + myPlayerID.getUniquePlayerID()).retrieve()
				.bodyToMono(ResponseEnvelope.class);

		ResponseEnvelope<GameState> result = getResponse(webAccess);

		return result.getData().get();
	}

	public boolean waitForStep() throws Exception {
		Thread.sleep(400);
		PlayerState ps = getMyPlayerState();
		logger.debug("network state in wait(): " + netConverter.toMyPlayerState(ps));
		while (netConverter.toMyPlayerState(ps) == client.controller.EMyClientState.MUSTWAIT) {
			ps = getMyPlayerState();
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	public void sendHalfMap(HalfMap myHalfMap) throws Exception {
		if (myHalfMap.isEmptyOrNull()) {
			logger.error("HalfMap in sendHalfMap() [ClientNetwork] is empty or null");
			throw new Exception("HalfMap in sendHalfMap() [ClientNetwork] is empty or null");
		}

		PlayerHalfMap networkHalfMap = new PlayerHalfMap(uniquePlayerID,
				netConverter.myHalfMapToNetworkHalfMap(myHalfMap));

		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + gameID + "/halfmaps")
				.body(BodyInserters.fromValue(networkHalfMap)).retrieve().bodyToMono(ResponseEnvelope.class);

		getResponse(webAccess);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <someType> ResponseEnvelope<someType> getResponse(Mono<ResponseEnvelope> webAccess) throws Exception {

		ResponseEnvelope<someType> response = webAccess.block();

		if (response.getState() == ERequestState.Error) {
			logger.error("getResponse() had error: " + response.getExceptionMessage());
			throw new Exception(response.getExceptionMessage());
		}

		return response;
	}

	public FullMap getMyFullMap() throws Exception {
		messagesbase.messagesfromserver.FullMap networkFullMap = getNetworkMap();
		return netConverter.networkMapToMyFullMap(networkFullMap.getMapNodes());
	}

	public UniquePlayerIdentifier getUniquePlayerID() {
		return uniquePlayerID;
	}

	@SuppressWarnings("rawtypes")
	public void sendMove(EMyMove move) throws Exception {
		if (move == null) {
			logger.error("nextMove received is null");
			throw new RuntimeException("nextMove received is null");
		}

		EMove moveToSend = netConverter.myMovetoNetworkMove(move);
		PlayerMove pm = PlayerMove.of(uniquePlayerID, moveToSend);

		Mono<ResponseEnvelope> webAccess = (baseWebClient.method(HttpMethod.POST)).uri("/" + gameID + "/moves")
				.header("accept", "application/xml").body(BodyInserters.fromValue(pm)).retrieve()
				.bodyToMono(ResponseEnvelope.class);

		getResponse(webAccess);
	}

	public Map<EMyGameEntity, Coordinate> getEntities() throws Exception {
		messagesbase.messagesfromserver.FullMap serverFullMap = getNetworkMap();
		Map<EMyGameEntity, Coordinate> entitiesFromNetworkMap = new HashMap<>();

		for (FullMapNode eachNode : serverFullMap.getMapNodes()) {
			Coordinate coord = new Coordinate(eachNode.getX(), eachNode.getY());
			for (EMyGameEntity eachEntity : netConverter.getGameEntitiesFromNetworkNode(eachNode, coord).keySet()) {
				entitiesFromNetworkMap.put(eachEntity, coord);
			}
		}
		return entitiesFromNetworkMap;
	}

	private messagesbase.messagesfromserver.FullMap getNetworkMap() throws Exception {
		return getGameState(uniquePlayerID).getMap();
	}

	public boolean treasureIsCollected() throws Exception {
		return getMyPlayerState().hasCollectedTreasure();
	}
}
