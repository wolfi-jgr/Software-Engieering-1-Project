package server.rules;

import java.util.Set;

import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.EPlayerGameState;
import server.eachgame.Game;
import server.exceptions.PlayerMustWaitException;
import server.player.Player;

public class WaitOrActRule implements IRule {

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
			throw new PlayerMustWaitException(playerHalfMap.getUniquePlayerID());
		}

	}

	@Override
	public void validateState(Set<Player> players, String gameID) {

	}

}
