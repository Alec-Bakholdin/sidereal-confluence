package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.model.BidTrackType;
import com.bakholdin.siderealconfluence.model.Confluence;
import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.model.Phase;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.RaceName;
import com.bakholdin.siderealconfluence.model.cards.CardType;
import com.bakholdin.siderealconfluence.service.GameStateSocketService;
import com.bakholdin.siderealconfluence.service.model.UpdateGameStateServerMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameStateService {
    private final CardService cardService;
    private final PlayerService playerService;
    private final ConfluenceService confluenceService;
    private final EconomyService economyService;
    private final GameStateSocketService gameStateSocketService;

    private GameState gameState = null;

    public GameState getGameState() {
        if (gameState == null) {
            return startNewGame();
        }
        return gameState;
    }

    public GameState startNewGame() {
        gameState = new GameState();
        cardService.resetCards();
        playerService.resetPlayers();
        return gameState;
    }

    public void addPlayerToGame(Player player) {
        gameState.getPlayers().put(player.getId(), player);
    }

    public Player addNewPlayerToGame(String playerName, RaceName raceName) {
        if (gameIsInSession()) {
            throw new RuntimeException("Game is already in session");
        }
        Player player = playerService.createPlayer(playerName, raceName);
        getGameState().getPlayers().put(player.getId(), player);
        return player;
    }

    public Confluence getCurrentConfluence() {
        GameState gameState = getGameState();
        int index = Math.max(0, gameState.getTurn() - 1);
        return gameState.getConfluenceList().get(index);
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

    public GameState startGame() {
        GameState gameState = getGameState();
        int numPlayers = gameState.getPlayers().size();

        gameState.setTurn(1);
        gameState.setPhase(Phase.Trade);
        gameState.setGameStarted(true);
        gameState.setGameOver(false);

        gameState.setConfluenceList(confluenceService.getConfluenceCards(numPlayers));

        gameState.setColonyBidTrack(confluenceService.getBidTrack(numPlayers, BidTrackType.Colony));
        gameState.setAvailableColonies(cardService.drawIds(numPlayers, CardType.Colony));

        gameState.setResearchTeamBidTrack(confluenceService.getBidTrack(numPlayers, BidTrackType.ResearchTeam));
        gameState.setAvailableResearchTeams(cardService.drawIds(numPlayers, CardType.ResearchTeam));

        return gameState;
    }

    public GameState advancePhase() {
        GameState gameState = getGameState();
        gameState.setPhase(getNextPhase(gameState.getPhase()));
        if (gameState.getPhase() == Phase.Trade) {
            advanceTurn(gameState);
        } else if (gameState.getPhase() == Phase.Confluence) {
            economyService.resolveEconomyStep();
        }
        return gameState;
    }

    private void advanceTurn(GameState gameState) {
        if (gameState.getTurn() == 6) {
            gameState.setGameOver(true);
        } else {
            gameState.setTurn(gameState.getTurn() + 1);
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
