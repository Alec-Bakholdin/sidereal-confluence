package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameStateService {
    private final CardService cardService;
    private GameState gameState = null;

    public GameState getGameState() {
        if (gameState == null) {
            cardService.resetCards();
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
