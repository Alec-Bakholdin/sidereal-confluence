package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.Confluence;
import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.model.Phase;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.RaceName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameStateService {
    private final CardService cardService;
    private final PlayerService playerService;
    private final ConfluenceService confluenceService;
    private GameState gameState = null;

    public GameState getGameState() {
        if (gameState == null) {
            return startNewGame();
        }
        return gameState;
    }

    public GameState startNewGame() {
        gameState = new GameState();
        cardService.resetCards();
        playerService.resetPlayers();
        return gameState;
    }

    public void addPlayerToGame(Player player) {
        gameState.getPlayers().put(player.getId(), player);
    }

    public Player addNewPlayerToGame(String playerName, RaceName raceName) {
        Player player = playerService.createPlayer(playerName, raceName);
        getGameState().getPlayers().put(player.getId(), player);
        return player;
    }

    public GameState startGame() {
        GameState gameState = getGameState();
        int numPlayers = gameState.getPlayers().size();
        List<Confluence> confluenceList = confluenceService.getConfluenceCards(numPlayers);
        gameState.setConfluenceList(confluenceList);
        gameState.setTurn(1);
        gameState.setPhase(Phase.Trade);
        gameState.setGameInSession(true);
        return gameState;
    }
}
