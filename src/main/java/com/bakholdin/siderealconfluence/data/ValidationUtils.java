package com.bakholdin.siderealconfluence.data;


import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.old_model.BidTrackType1;
import com.bakholdin.siderealconfluence.old_model.GameState1;
import com.bakholdin.siderealconfluence.old_model.Phase1;
import com.bakholdin.siderealconfluence.old_model.Player1;
import com.bakholdin.siderealconfluence.old_model.PlayerBid1;
import com.bakholdin.siderealconfluence.old_model.Resources1;
import com.bakholdin.siderealconfluence.old_model.cards.Card1;
import com.bakholdin.siderealconfluence.old_model.cards.CardType1;

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

    protected static void validateGameIsNotInSession(GameState1 gameState) {
        if (gameState.isGameStarted() && !gameState.isGameOver()) {
            throw new RuntimeException("Game is already in session");
        }
    }

    protected static void validateCardExists(CardService cardService, String cardId) {
        if (!cardService.contains(cardId)) {
            throw new IllegalArgumentException("Card with id " + cardId + " does not exist");
        }
    }

    public static void validateCardType(CardType1 type1, CardType1 type2, Card1 card1) {
        if (card1.getType() != type1 && card1.getType() != type2) {
            throw new IllegalArgumentException("Card must be " + type1 + " or " + type2);
        }
    }

    public static void validateCardType(CardType1 type, Card1 card1) {
        if (card1.getType() != type) {
            throw new IllegalArgumentException("Card must be " + type);
        }
    }

    public static void validatePlayerCount(int numPlayers) {
        if (numPlayers < 4 || numPlayers > 9) {
            throw new UnsupportedOperationException("Number of players must be between 4 and 9, inclusive");
        }
    }

    public static void validatePhase(GameStateService gameStateService, Phase1 phase) {
        if (!gameStateService.gameIsInSession() || gameStateService.getGameState().getPhase() != phase) {
            throw new UnsupportedOperationException("Cannot perform operation unless in " + phase + " phase");
        }
    }

    public static void validatePlayerHasEnoughShips(PlayerService playerService, PlayerBid1 playerBid) {
        validatePlayerExists(playerService, playerBid.getPlayerId());
        Player1 player = playerService.get(playerBid.getPlayerId());
        if (player.getResources().getShips() < playerBid.getColonyBid() + playerBid.getResearchTeamBid()) {
            throw new IllegalArgumentException(player.getName() + " does not have enough ships");
        }
    }

    public static void validateCardIdIsPresentInList(List<String> cardList, String cardId) {
        if (!cardList.contains(cardId)) {
            throw new IllegalArgumentException("Card with id " + cardId + " does not exist in list");
        }
    }

    public static void validateConfluenceCardPresentInProperTrack(GameState1 gameState, Card1 card1) {
        if (gameState.getActiveBidTrack() == null) {
            throw new IllegalArgumentException("Bid track is null");
        }
        if (gameState.getActiveBidTrack() == BidTrackType1.ResearchTeam) {
            validateCardType(CardType1.ResearchTeam, card1);
            validateCardIdIsPresentInList(gameState.getAvailableResearchTeams(), card1.getId());
        } else if (gameState.getActiveBidTrack() == BidTrackType1.Colony) {
            validateCardType(CardType1.Colony, card1);
            validateCardIdIsPresentInList(gameState.getAvailableColonies(), card1.getId());
        }
    }

    public static void validatePlayerHasEnoughResources(Player1 player, Resources1 cost) {
        if (!player.getResources().canPayFor(cost)) {
            throw new UnsupportedOperationException("Player does not have enough resources");
        }
    }

    public static void validateNonNullBidTrack(BidTrackType1 activeBidTrack) {
        if (activeBidTrack == null) {
            throw new IllegalArgumentException("Bid track is null");
        }
    }

    public static void validatePlayerBidHighEnough(GameState1 gameState, Player1 player, Card1 card1) {
        validateNonNullBidTrack(gameState.getActiveBidTrack());
        boolean isColonyTrack = gameState.getActiveBidTrack() == BidTrackType1.Colony;
        List<String> cardIdList = isColonyTrack ? gameState.getAvailableColonies() : gameState.getAvailableResearchTeams();
        List<Integer> bidTrack = isColonyTrack ? gameState.getColonyBidTrack() : gameState.getResearchTeamBidTrack();
        validateCardIdIsPresentInList(cardIdList, card1.getId());
        int index = cardIdList.indexOf(card1.getId());
        int minNecessaryShips = bidTrack.get(index);
        PlayerBid1 playerBid = player.getPlayerBid();
        double bidShips = isColonyTrack ? playerBid.getColonyBid() : playerBid.getResearchTeamBid();
        if (bidShips < minNecessaryShips) {
            throw new IllegalArgumentException("Player bid is too low");
        }
    }
}
