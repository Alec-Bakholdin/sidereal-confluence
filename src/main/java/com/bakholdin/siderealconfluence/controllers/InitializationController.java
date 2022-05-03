package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.data.CardService;
import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.model.cards.Card;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
public class InitializationController {
    private final CardService cardService;

    @GetMapping("/initializeGame")
    public GameState initializeGame() {
        return new GameState();
    }

    @GetMapping("/allCards")
    public Map<String, Card> getAllCards() {
        return cardService.getCards();
    }
}
