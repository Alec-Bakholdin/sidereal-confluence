package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.controllers.model.SocketTopics;
import com.bakholdin.siderealconfluence.controllers.model.UpdateGameStateServerMessage;
import com.bakholdin.siderealconfluence.data.GameStateService;
import com.bakholdin.siderealconfluence.model.GameState;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GameStateController {

    private final GameStateService gameStateService;

    @MessageMapping(SocketTopics.APP_START_GAME)
    @SendTo(SocketTopics.TOPIC_UPDATE_GAME_STATE)
    public UpdateGameStateServerMessage startGame() {
        GameState gameState = gameStateService.startGame();
        return UpdateGameStateServerMessage.builder()
                .phase(gameState.getPhase())
                .turn(gameState.getTurn())
                .isGameOver(gameState.isGameOver())
                .isGameStarted(gameState.isGameStarted())
                .confluenceList(gameState.getConfluenceList())
                .build();
    }
}
