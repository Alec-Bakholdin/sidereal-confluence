package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.model.BidTrackType;
import com.bakholdin.siderealconfluence.model.Confluence;
import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.model.Phase;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.RaceName;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.CardType;
import com.bakholdin.siderealconfluence.service.CardSocketService;
import com.bakholdin.siderealconfluence.service.GameStateSocketService;
import com.bakholdin.siderealconfluence.service.model.UpdateGameStateServerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private GameState gameState = null;

    public GameState getGameState() {
        if (gameState == null) {
            return resetGame(false);
        }
        return gameState;
    }

    public GameState resetGame(boolean keepPlayers) {
        cardService.resetCards();
        if (gameState == null || !keepPlayers) {
            gameState = new GameState();
            playerService.resetPlayers();
        }
        return gameState;
    }

    public void addPlayerToGame(Player player) {
        gameState.getPlayers().put(player.getId(), player);
    }

    public Player addNewPlayerToGame(String playerName, RaceName raceName) {
        ValidationUtils.validateGameIsNotInSession(getGameState());

        Player player = playerService.createPlayer(playerName, raceName);
        getGameState().getPlayers().put(player.getId(), player);
        return player;
    }

    public Confluence getCurrentConfluence() {
        GameState gameState = getGameState();
        int index = Math.max(0, gameState.getTurn() - 1);
        return gameState.getConfluenceList().get(index);
    }

    public void removeConfluenceCard(String cardId) {
        ValidationUtils.validateCardExists(cardService, cardId);
        Card card = cardService.get(cardId);
        GameState gameState = getGameState();
        ValidationUtils.validateCardType(CardType.ResearchTeam, CardType.Colony, card);

        List<String> cards = card.getType() == CardType.ResearchTeam ? gameState.getAvailableResearchTeams() : gameState.getAvailableColonies();
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
        GameState gameState = getGameState();
        gameState.getPendingResearches().add(technologyName);
        gameStateSocketService.updateGameState(UpdateGameStateServerMessage.builder()
                .pendingResearches(gameState.getPendingResearches())
                .build());
    }

    public boolean gameIsInSession() {
        return getGameState().isGameStarted() && !gameState.isGameOver();
    }

    public void startGame() {
        GameState gameState = resetGame(true);
        int numPlayers = gameState.getPlayers().size();
        ValidationUtils.validatePlayerCount(numPlayers);

        gameState.setTurn(1);
        gameState.setPhase(Phase.Trade);
        gameState.setGameStarted(true);
        gameState.setGameOver(false);

        gameState.setConfluenceList(confluenceService.getConfluenceCards(numPlayers));

        gameState.setColonyBidTrack(confluenceService.getBidTrack(numPlayers, BidTrackType.Colony));
        gameState.setAvailableColonies(cardService.drawIds(numPlayers, CardType.Colony));

        gameState.setResearchTeamBidTrack(confluenceService.getBidTrack(numPlayers, BidTrackType.ResearchTeam));
        gameState.setAvailableResearchTeams(cardService.drawIds(numPlayers, CardType.ResearchTeam));

        for (Player player : gameState.getPlayers().values()) {
            playerService.resetPlayerWithoutSocket(player.getId());
        }

        gameStateSocketService.updateGameStateWholesale(gameState);
        cardSocketService.updateAllCards(cardService.getCurrentGameCards());
    }

    public void advancePhase() {
        UpdateGameStateServerMessage.Builder msgBuilder = UpdateGameStateServerMessage.builder();

        GameState gameState = getGameState();
        gameState.setPhase(getNextPhase(gameState.getPhase()));
        msgBuilder.phase(gameState.getPhase());

        if (gameState.getPhase() == Phase.Trade) {
            advanceTurn(gameState);
            msgBuilder.turn(gameState.getTurn());
            msgBuilder.availableColonies(gameState.getAvailableColonies());
            msgBuilder.availableResearchTeams(gameState.getAvailableResearchTeams());
            msgBuilder.pendingResearches(gameState.getPendingResearches());
        } else if (gameState.getPhase() == Phase.Confluence) {
            economyService.resolveEconomyStep();
        }

        msgBuilder.gameOver(gameState.isGameOver());
        gameStateSocketService.updateGameState(msgBuilder.build());
    }

    private void advanceTurn(GameState gameState) {
        if (gameState.getTurn() == 6) {
            gameState.setGameOver(true);
            return;
        }

        gameState.setTurn(gameState.getTurn() + 1);
        resetBidTrack(gameState.getAvailableColonies(), gameState.getColonyBidTrack(), BidTrackType.Colony);
        resetBidTrack(gameState.getAvailableResearchTeams(), gameState.getResearchTeamBidTrack(), BidTrackType.ResearchTeam);
        for (String pendingTech : gameState.getPendingResearches()) {
            for (Player player : gameState.getPlayers().values()) {
                try {
                    playerService.tryAcquireTechnology(player.getId(), pendingTech);
                } catch (UnsupportedOperationException e) {
                    log.error("Could not acquire technology {} for player {}: {}", pendingTech, player.getId(), e.getMessage());
                }
            }
        }
        gameState.getPendingResearches().clear();
    }

    private void resetBidTrack(List<String> availableCards, List<Integer> bidTrack, BidTrackType bidTrackType) {
        CardType cardType = bidTrackType == BidTrackType.Colony ? CardType.Colony : CardType.ResearchTeam;
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
        for (Card card : cardService.draw(bidTrack.size() - cardsLeft, cardType)) {
            availableCards.set(cardsLeft++, card.getId());
        }
    }

    private Phase getNextPhase(Phase phase) {
        switch (phase) {
            case Trade:
                return Phase.Economy;
            case Economy:
                return Phase.Confluence;
            case Confluence:
                return Phase.Trade;
            default:
                throw new IllegalArgumentException("Unknown phase: " + phase);
        }
    }
}
