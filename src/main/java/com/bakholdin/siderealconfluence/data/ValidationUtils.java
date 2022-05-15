package com.bakholdin.siderealconfluence.data;


import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.model.BidTrackType;
import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.model.Phase;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.PlayerBid;
import com.bakholdin.siderealconfluence.model.Resources;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.CardType;

import java.util.List;
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
        if (!playerService.ownsCard(playerId, cardId)) {
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

    public static void validatePlayerHasEnoughShips(PlayerService playerService, PlayerBid playerBid) {
        validatePlayerExists(playerService, playerBid.getPlayerId());
        Player player = playerService.get(playerBid.getPlayerId());
        if (player.getResources().getShips() < playerBid.getColonyBid() + playerBid.getResearchTeamBid()) {
            throw new IllegalArgumentException(player.getName() + " does not have enough ships");
        }
    }

    public static void validateCardIdIsPresentInList(List<String> cardList, String cardId) {
        if (!cardList.contains(cardId)) {
            throw new IllegalArgumentException("Card with id " + cardId + " does not exist in list");
        }
    }

    public static void validateConfluenceCardPresentInProperTrack(GameState gameState, Card card) {
        if (gameState.getActiveBidTrack() == null) {
            throw new IllegalArgumentException("Bid track is null");
        }
        if (gameState.getActiveBidTrack() == BidTrackType.ResearchTeam) {
            validateCardType(CardType.ResearchTeam, card);
            validateCardIdIsPresentInList(gameState.getAvailableResearchTeams(), card.getId());
        } else if (gameState.getActiveBidTrack() == BidTrackType.Colony) {
            validateCardType(CardType.Colony, card);
            validateCardIdIsPresentInList(gameState.getAvailableColonies(), card.getId());
        }
    }

    public static void validatePlayerHasEnoughResources(Player player, Resources cost) {
        if (!player.getResources().canPayFor(cost)) {
            throw new UnsupportedOperationException("Player does not have enough resources");
        }
    }

    public static void validateNonNullBidTrack(BidTrackType activeBidTrack) {
        if (activeBidTrack == null) {
            throw new IllegalArgumentException("Bid track is null");
        }
    }

    public static void validatePlayerBidHighEnough(GameState gameState, Player player, Card card) {
        validateNonNullBidTrack(gameState.getActiveBidTrack());
        boolean isColonyTrack = gameState.getActiveBidTrack() == BidTrackType.Colony;
        List<String> cardIdList = isColonyTrack ? gameState.getAvailableColonies() : gameState.getAvailableResearchTeams();
        List<Integer> bidTrack = isColonyTrack ? gameState.getColonyBidTrack() : gameState.getResearchTeamBidTrack();
        validateCardIdIsPresentInList(cardIdList, card.getId());
        int index = cardIdList.indexOf(card.getId());
        int minNecessaryShips = bidTrack.get(index);
        PlayerBid playerBid = player.getPlayerBid();
        double bidShips = isColonyTrack ? playerBid.getColonyBid() : playerBid.getResearchTeamBid();
        if (bidShips < minNecessaryShips) {
            throw new IllegalArgumentException("Player bid is too low");
        }
    }
}
