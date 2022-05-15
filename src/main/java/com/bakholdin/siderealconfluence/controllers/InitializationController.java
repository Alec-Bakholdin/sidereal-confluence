package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.controllers.model.JoinGamePayload;
import com.bakholdin.siderealconfluence.controllers.model.JoinGameResponse;
import com.bakholdin.siderealconfluence.controllers.model.RejoinGamePayload;
import com.bakholdin.siderealconfluence.data.GameStateService;
import com.bakholdin.siderealconfluence.data.PlayerService;
import com.bakholdin.siderealconfluence.data.RaceService;
import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.service.model.OutgoingSocketTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@Log4j2
@CrossOrigin(origins = {"http://localhost:3000", "http://71.187.248.226:3000"})
@RestController()
@RequiredArgsConstructor
public class InitializationController {
    private final GameStateService gameStateService;
    private final CardService cardService;
    private final PlayerService playerService;
    private final RaceService raceService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/getGame")
    public GameState getGame() {
        return gameStateService.getGameState();
    }

    @PostMapping("/startNewGame")
    public GameState startNewGame() {
        return gameStateService.resetGame(false);
    }

    @GetMapping("/allCards")
    public Map<String, Card> getAllCards() {
        return cardService.getCurrentGameCards();
    }

    @PostMapping("/joinGame")
    public JoinGameResponse joinGame(@RequestBody JoinGamePayload payload) {
        if (payload.getPlayerName() == null || payload.getPlayerName().isEmpty()) {
            throw new RuntimeException("Player name cannot be empty");
        }
        if (gameStateService.gameIsInSession()) {
            throw new RuntimeException("Game is already in session");
        }
        if (gameStateService.getGameState().getPlayers().size() >= 9) {
            throw new RuntimeException("Game is full");
        }
        Player player = gameStateService.addNewPlayerToGame(payload.getPlayerName(), payload.getRaceName());
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_PLAYER_JOINED_GAME, player);
        return JoinGameResponse.builder()
                .gameState(gameStateService.getGameState())
                .playerName(player.getName())
                .playerId(player.getId())
                .raceName(player.getRace().getName())
                .build();
    }

    @PostMapping("/rejoinGame")
    public JoinGameResponse rejoinGame(@RequestBody RejoinGamePayload payload) {
        UUID playerUUID = UUID.fromString(payload.getPlayerId());
        Player player = gameStateService.getGameState().getPlayers().get(playerUUID);
        if (player == null) {
            player = gameStateService.addNewPlayerToGame(payload.getPlayerName(), payload.getRaceName());
        }
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_PLAYER_JOINED_GAME, player);
        return JoinGameResponse.builder()
                .playerId(player.getId())
                .playerName(player.getName())
                .raceName(player.getRace().getName())
                .gameState(gameStateService.getGameState())
                .build();
    }
}
