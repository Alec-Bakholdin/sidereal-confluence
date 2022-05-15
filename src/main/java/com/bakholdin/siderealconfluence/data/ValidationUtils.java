package com.bakholdin.siderealconfluence.data;


import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.model.Phase;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.CardType;

import java.util.UUID;

public class ValidationUtils {

    protected static void validatePlayerExists(PlayerService playerService, String playerId) {
        validateNonNullPlayerId(playerId);
        validatePlayerExists(playerService, UUID.fromString(playerId));
    }

    protected static void validatePlayerExists(PlayerService playerService, UUID playerId) {
        if (!playerService.contains(playerId)) {
            throw new IllegalArgumentException("Player does not exist");
        }
    }

    protected static void validateNonNullPlayerId(String playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("PlayerId cannot be null");
        }
    }

    protected static void validateOwnsCard(PlayerService playerService, String playerId, String cardId) {
        validateNonNullPlayerId(playerId);
        validateOwnsCard(playerService, UUID.fromString(playerId), cardId);
    }

    protected static void validateOwnsCard(PlayerService playerService, UUID playerId, String cardId) {
        if (playerService.ownsCard(playerId, cardId)) {
            throw new IllegalArgumentException("Card does not exist");
        }
    }

    protected static void validateCardIsActive(PlayerService playerService, String playerId, String cardId) {
        validateNonNullPlayerId(playerId);
        validateCardIsActive(playerService, UUID.fromString(playerId), cardId);
    }

    protected static void validateCardIsActive(PlayerService playerService, UUID playerId, String cardId) {
        if (!playerService.hasCardActive(playerId, cardId)) {
            throw new IllegalArgumentException("Current owner does not own card with id " + cardId);
        }
    }

    protected static void validateGameIsNotInSession(GameState gameState) {
        if (gameState.isGameStarted() && !gameState.isGameOver()) {
            throw new RuntimeException("Game is already in session");
        }
    }

    protected static void validateCardExists(CardService cardService, String cardId) {
        if (!cardService.contains(cardId)) {
            throw new IllegalArgumentException("Card with id " + cardId + " does not exist");
        }
    }

    public static void validateCardType(CardType type1, CardType type2, Card card) {
        if (card.getType() != type1 && card.getType() != type2) {
            throw new IllegalArgumentException("Card must be " + type1 + " or " + type2);
        }
    }

    public static void validateCardType(CardType type, Card card) {
        if (card.getType() != type) {
            throw new IllegalArgumentException("Card must be " + type);
        }
    }

    public static void validatePlayerCount(int numPlayers) {
        if (numPlayers < 4 || numPlayers > 9) {
            throw new UnsupportedOperationException("Number of players must be between 4 and 9, inclusive");
        }
    }

    public static void validatePhase(GameStateService gameStateService, Phase phase) {
        if (!gameStateService.gameIsInSession() || gameStateService.getGameState().getPhase() != phase) {
            throw new UnsupportedOperationException("Cannot perform operation unless in " + phase + " phase");
        }
    }
}
