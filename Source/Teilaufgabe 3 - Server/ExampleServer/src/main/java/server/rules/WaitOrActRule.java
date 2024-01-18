package server.rules;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.EPlayerGameState;
import server.eachgame.Game;
import server.exceptions.PlayerMustWaitException;
import server.player.Player;

public class WaitOrActRule implements IRule {
	
	private final static Logger logger = LoggerFactory.getLogger(WaitOrActRule.class);

	@Override
	public void validateNewGame() {
		// TODO Auto-generated method stub

	}

	@Override
	public void validatePlayerRegistration(Set<Player> players) {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateHalfMap(PlayerHalfMap playerHalfMap, Game game) {
		
		if(game.getPlayer(playerHalfMap.getUniquePlayerID()).getPlayerState() != EPlayerGameState.MustAct) {
			logger.warn("the player: " + playerHalfMap.getUniquePlayerID() + " has sent a HalfMap but it was not his turn to send..");
			throw new PlayerMustWaitException(playerHalfMap.getUniquePlayerID());
		}

	}

	@Override
	public void validateState(Set<Player> players, String gameID) {

	}

}
