package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.model.Player;
import org.springframework.stereotype.Service;

@Service
public class GameStateService {
    private GameState gameState = null;

    public GameState getGameState() {
        if (gameState == null) {
            return startNewGame();
        }
        return gameState;
    }

    public GameState startNewGame() {
        gameState = new GameState();
        return gameState;
    }

    public void addPlayerToGame(GameState gameState, Player player) {
        gameState.getPlayers().put(player.getId(), player);
    }
}
