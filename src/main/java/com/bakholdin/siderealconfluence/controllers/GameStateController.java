package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.controllers.model.IncomingSocketTopics;
import com.bakholdin.siderealconfluence.controllers.model.UpdateGameStateServerMessage;
import com.bakholdin.siderealconfluence.data.GameStateService;
import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.service.model.OutgoingSocketTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GameStateController {

    private final GameStateService gameStateService;

    @MessageMapping(IncomingSocketTopics.APP_START_GAME)
    @SendTo(OutgoingSocketTopics.TOPIC_UPDATE_GAME_STATE)
    public UpdateGameStateServerMessage startGame() {
        GameState gameState = gameStateService.startGame();
        return UpdateGameStateServerMessage.builder()
                .phase(gameState.getPhase())
                .turn(gameState.getTurn())
                .isGameOver(gameState.isGameOver())
                .isGameStarted(gameState.isGameStarted())
                .confluenceList(gameState.getConfluenceList())
                .availableResearchTeams(gameState.getAvailableResearchTeams())
                .researchTeamBidTrack(gameState.getResearchTeamBidTrack())
                .availableColonies(gameState.getAvailableColonies())
                .colonyBidTrack(gameState.getColonyBidTrack())
                .build();
    }

    @MessageMapping(IncomingSocketTopics.APP_NEXT_PHASE)
    @SendTo(OutgoingSocketTopics.TOPIC_UPDATE_GAME_STATE)
    public UpdateGameStateServerMessage nextPhase() {
        if (gameStateService.getGameState().isGameOver()) {
            throw new UnsupportedOperationException("Game is over");
        }
        GameState gameState = gameStateService.advancePhase();
        return UpdateGameStateServerMessage.builder()
                .phase(gameState.getPhase())
                .turn(gameState.getTurn())
                .isGameOver(gameState.isGameOver())
                .build();
    }
}
