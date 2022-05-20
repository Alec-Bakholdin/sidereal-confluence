package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.old_model.BidTrackType1;
import com.bakholdin.siderealconfluence.old_model.Confluence1;
import com.bakholdin.siderealconfluence.old_model.GameState1;
import com.bakholdin.siderealconfluence.old_model.Phase1;
import com.bakholdin.siderealconfluence.old_model.Player1;
import com.bakholdin.siderealconfluence.old_model.PlayerBid1;
import com.bakholdin.siderealconfluence.old_model.RaceName1;
import com.bakholdin.siderealconfluence.old_model.cards.Card1;
import com.bakholdin.siderealconfluence.old_model.cards.CardType1;
import com.bakholdin.siderealconfluence.old_service.CardSocketService;
import com.bakholdin.siderealconfluence.old_service.GameStateSocketService;
import com.bakholdin.siderealconfluence.old_service.model.UpdateGameStateServerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class GameStateService {
    private final CardService cardService;
    private final PlayerService playerService;
    private final ConfluenceService confluenceService;
    private final EconomyService economyService;
    private final GameStateSocketService gameStateSocketService;
    private final CardSocketService cardSocketService;

    private GameState1 gameState = null;

    public GameState1 getGameState() {
        if (gameState == null) {
            return resetGame(false);
        }
        return gameState;
    }

    public GameState1 resetGame(boolean keepPlayers) {
        cardService.resetCards();
        if (gameState == null || !keepPlayers) {
            gameState = new GameState1();
            playerService.resetPlayers();
        }
        return gameState;
    }

    public void addPlayerToGame(Player1 player) {
        gameState.getPlayers().put(player.getId(), player);
    }

    public Player1 addNewPlayerToGame(String playerName, RaceName1 raceName) {
        ValidationUtils.validateGameIsNotInSession(getGameState());

        Player1 player = playerService.createPlayer(playerName, raceName);
        getGameState().getPlayers().put(player.getId(), player);
        return player;
    }

    public Confluence1 getCurrentConfluence() {
        GameState1 gameState = getGameState();
        int index = Math.max(0, gameState.getTurn() - 1);
        return gameState.getConfluence1List().get(index);
    }

    public void removeConfluenceCard(String cardId) {
        ValidationUtils.validateCardExists(cardService, cardId);
        Card1 card1 = cardService.get(cardId);
        GameState1 gameState = getGameState();
        ValidationUtils.validateCardType(CardType1.ResearchTeam, CardType1.Colony, card1);

        List<String> cards = card1.getType() == CardType1.ResearchTeam ? gameState.getAvailableResearchTeams() : gameState.getAvailableColonies();
        int index = cards.indexOf(cardId);
        if (index == -1) {
            throw new IllegalArgumentException("Card with id " + cardId + " is not available in confluence bid track");
        }
        cards.set(index, null);
        UpdateGameStateServerMessage msg = UpdateGameStateServerMessage.builder()
                .availableColonies(gameState.getAvailableColonies())
                .availableResearchTeams(gameState.getAvailableResearchTeams())
                .build();
        gameStateSocketService.updateGameState(msg);
    }

    public void addResearchedTechnology(String technologyName) {
        GameState1 gameState = getGameState();
        gameState.getPendingResearches().add(technologyName);
        gameStateSocketService.updateGameState(UpdateGameStateServerMessage.builder()
                .pendingResearches(gameState.getPendingResearches())
                .build());
    }

    public boolean gameIsInSession() {
        return getGameState().isGameStarted() && !gameState.isGameOver();
    }

    public void setPlayerReadyStatus(String playerId, boolean ready) {
        GameState1 gameState = getGameState();
        // don't update ready status if currently resolving bids
        if (gameState.getActiveBidTrack() != null) {
            return;
        }
        playerService.setReadyStatus(playerId, ready);
        for (Player1 player : gameState.getPlayers().values()) {
            if (!player.isReady()) {
                return;
            }
        }
        // only if all players are ready
        if (gameState.getPhase() == Phase1.Confluence) {
            List<PlayerBid1> playerBids = gameState.getPlayers().values().stream()
                    .map(Player1::getPlayerBid)
                    .filter(bid -> bid != null && (bid.getColonyBid() > 0 || bid.getResearchTeamBid() > 0))
                    .collect(Collectors.toList());
            gameStateSocketService.revealPlayerBids(playerBids);
            advanceBids();
        } else {
            advancePhase();
        }
    }

    public void advanceBids() {
        GameState1 gameState = getGameState();

        if (gameState.getActiveBidder() != null) {
            Player1 player = playerService.get(gameState.getActiveBidder());
            ValidationUtils.validateNonNullBidTrack(gameState.getActiveBidTrack());
            boolean isColonyTrack = gameState.getActiveBidTrack() == BidTrackType1.Colony;
            if (isColonyTrack) {
                playerService.setPlayerBid(player.getId(), 0, player.getPlayerBid().getResearchTeamBid());
            } else {
                playerService.setPlayerBid(player.getId(), player.getPlayerBid().getColonyBid(), 0);
            }
        }

        List<PlayerBid1> playerBids = gameState.getPlayers().values().stream()
                .map(Player1::getPlayerBid)
                .filter(bid -> bid != null && (bid.getColonyBid() > 0 || bid.getResearchTeamBid() > 0))
                .collect(Collectors.toList());
        List<PlayerBid1> colonyBids = playerBids.stream()
                .filter(bid -> bid.getColonyBid() > 0)
                .sorted(Comparator.comparing(PlayerBid1::getColonyBid).reversed())
                .collect(Collectors.toList());
        List<PlayerBid1> researchTeamBids = playerBids.stream()
                .filter(bid -> bid.getResearchTeamBid() > 0)
                .sorted(Comparator.comparing(PlayerBid1::getResearchTeamBid).reversed())
                .collect(Collectors.toList());
        if (colonyBids.size() > 0) {
            gameState.setActiveBidTrack(BidTrackType1.Colony);
            gameState.setActiveBidder(colonyBids.get(0).getPlayerId());
            colonyBids.remove(0);
        } else if (researchTeamBids.size() > 0) {
            gameState.setActiveBidTrack(BidTrackType1.ResearchTeam);
            gameState.setActiveBidder(researchTeamBids.get(0).getPlayerId());
            researchTeamBids.remove(0);
        } else {
            advancePhase();
        }
        UpdateGameStateServerMessage msg = UpdateGameStateServerMessage.builder()
                .activeBidTrack(gameState.getActiveBidTrack())
                .activeBidder(gameState.getActiveBidder())
                .build();
        gameStateSocketService.updateGameState(msg);
    }

    public void startGame() {
        GameState1 gameState = resetGame(true);
        int numPlayers = gameState.getPlayers().size();
        ValidationUtils.validatePlayerCount(numPlayers);

        gameState.setTurn(1);
        gameState.setPhase(Phase1.Trade);
        gameState.setGameStarted(true);
        gameState.setGameOver(false);

        gameState.setConfluence1List(confluenceService.getConfluenceCards(numPlayers));

        gameState.setColonyBidTrack(confluenceService.getBidTrack(numPlayers, BidTrackType1.Colony));
        gameState.setAvailableColonies(cardService.drawIds(numPlayers, CardType1.Colony));

        gameState.setResearchTeamBidTrack(confluenceService.getBidTrack(numPlayers, BidTrackType1.ResearchTeam));
        gameState.setAvailableResearchTeams(cardService.drawIds(numPlayers, CardType1.ResearchTeam));

        for (Player1 player : gameState.getPlayers().values()) {
            playerService.resetPlayerWithoutSocket(player.getId());
        }

        gameStateSocketService.updateGameStateWholesale(gameState);
        cardSocketService.updateAllCards(cardService.getCurrentGameCards());
    }

    public void advancePhase() {
        UpdateGameStateServerMessage.Builder msgBuilder = UpdateGameStateServerMessage.builder();

        GameState1 gameState = getGameState();
        gameState.setPhase(getNextPhase(gameState.getPhase()));
        msgBuilder.phase(gameState.getPhase());

        if (gameState.getPhase() == Phase1.Trade) {
            advanceTurn(gameState, msgBuilder);
        } else if (gameState.getPhase() == Phase1.Confluence) {
            economyService.resolveEconomyStep();
        }
        for (Player1 player : gameState.getPlayers().values()) {
            playerService.setReadyStatus(player.getId(), false);
        }

        msgBuilder.gameOver(gameState.isGameOver());
        gameStateSocketService.updateGameState(msgBuilder.build());
    }

    private void advanceTurn(GameState1 gameState, UpdateGameStateServerMessage.Builder msgBuilder) {
        if (gameState.getTurn() == 6) {
            gameState.setGameOver(true);
            msgBuilder.gameOver(true);
            return;
        }

        gameState.setTurn(gameState.getTurn() + 1);
        msgBuilder.turn(gameState.getTurn());

        resetBidTrack(gameState.getAvailableColonies(), gameState.getColonyBidTrack(), BidTrackType1.Colony);
        msgBuilder.availableColonies(gameState.getAvailableColonies());
        msgBuilder.colonyBidTrack(gameState.getColonyBidTrack());

        resetBidTrack(gameState.getAvailableResearchTeams(), gameState.getResearchTeamBidTrack(), BidTrackType1.ResearchTeam);
        msgBuilder.availableResearchTeams(gameState.getAvailableResearchTeams());
        msgBuilder.researchTeamBidTrack(gameState.getResearchTeamBidTrack());

        for (String pendingTech : gameState.getPendingResearches()) {
            for (Player1 player : gameState.getPlayers().values()) {
                try {
                    playerService.tryAcquireTechnology(player.getId(), pendingTech);
                } catch (UnsupportedOperationException e) {
                    log.error("Could not acquire technology {} for player {}: {}", pendingTech, player.getId(), e.getMessage());
                }
            }
        }
        gameState.getPendingResearches().clear();
        msgBuilder.pendingResearches(gameState.getPendingResearches());

        gameState.setActiveBidder(null);
        msgBuilder.activeBidder(null);
        gameState.setActiveBidTrack(null);
        msgBuilder.activeBidTrack(null);
    }

    private void resetBidTrack(List<String> availableCards, List<Integer> bidTrack, BidTrackType1 bidTrackType1) {
        CardType1 cardType1 = bidTrackType1 == BidTrackType1.Colony ? CardType1.Colony : CardType1.ResearchTeam;
        if (availableCards.size() != bidTrack.size()) {
            throw new IllegalArgumentException("availableCards and bidTrack must be the same size");
        }
        int cardsLeft = 0;
        for (int i = 0; i < availableCards.size(); i++) {
            if (bidTrack.get(i) == 1) {
                availableCards.set(0, null);
            } else if (availableCards.get(i) != null) {
                availableCards.set(cardsLeft++, availableCards.get(i));
            }
        }
        for (Card1 card1 : cardService.draw(bidTrack.size() - cardsLeft, cardType1)) {
            availableCards.set(cardsLeft++, card1.getId());
        }
    }

    private Phase1 getNextPhase(Phase1 phase) {
        switch (phase) {
            case Trade:
                return Phase1.Economy;
            case Economy:
                return Phase1.Confluence;
            case Confluence:
                return Phase1.Trade;
            default:
                throw new IllegalArgumentException("Unknown phase: " + phase);
        }
    }

    public void skipBid() {
        ValidationUtils.validatePhase(this, Phase1.Confluence);
        ValidationUtils.validateNonNullBidTrack(getGameState().getActiveBidTrack());
        advanceBids();
    }
}
