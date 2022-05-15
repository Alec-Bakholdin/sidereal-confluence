package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.controllers.model.IncomingSocketTopics;
import com.bakholdin.siderealconfluence.data.GameStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GameStateController {

    private final GameStateService gameStateService;

    @MessageMapping(IncomingSocketTopics.APP_START_GAME)
    public void startGame() {
        if (gameStateService.gameIsInSession()) {
            throw new UnsupportedOperationException("Game is already in session");
        }
        gameStateService.startGame();
    }

    @MessageMapping(IncomingSocketTopics.APP_NEXT_PHASE)
    public void nextPhase() {
        if (gameStateService.getGameState().isGameOver()) {
            throw new UnsupportedOperationException("Game is over");
        }
        gameStateService.advancePhase();
    }
}
