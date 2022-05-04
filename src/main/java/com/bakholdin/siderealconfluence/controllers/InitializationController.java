package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.controllers.model.JoinGamePayload;
import com.bakholdin.siderealconfluence.data.CardService;
import com.bakholdin.siderealconfluence.data.GameStateService;
import com.bakholdin.siderealconfluence.data.PlayerService;
import com.bakholdin.siderealconfluence.data.RaceService;
import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.RaceEnum;
import com.bakholdin.siderealconfluence.model.cards.Card;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Log4j2
@CrossOrigin(origins = "http://localhost:3000")
@RestController()
@RequiredArgsConstructor
public class InitializationController {
    private final GameStateService gameStateService;
    private final CardService cardService;
    private final PlayerService playerService;
    private final RaceService raceService;

    @GetMapping("/getGame")
    public GameState getGame() {
        return gameStateService.getGameState();
    }

    @PostMapping("/startNewGame")
    public GameState startNewGame() {
        return gameStateService.startNewGame();
    }

    @GetMapping("/allCards")
    public Map<String, Card> getAllCards() {
        return cardService.getCurrentGameCards();
    }

    @PostMapping("/joinGame")
    public Player joinGame(@RequestBody JoinGamePayload payload) {
        Player player = playerService.createPlayer(payload.getPlayerName(), raceService.get(RaceEnum.Caylion));
        gameStateService.addPlayerToGame(gameStateService.getGameState(), player);
        return player;
    }
}
