package com.bakholdin.siderealconfluence.data.cardActions;

import com.bakholdin.siderealconfluence.data.GameStateService;
import com.bakholdin.siderealconfluence.data.PlayerService;
import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.model.Converter;
import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.model.Phase;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.Resources;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.CardType;
import com.bakholdin.siderealconfluence.model.cards.Colony;
import com.bakholdin.siderealconfluence.model.cards.ConverterCard;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam;
import com.bakholdin.siderealconfluence.service.CardSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class CardActionService {
    private final CardService cardService;
    private final GameStateService gameStateService;
    private final PlayerService playerService;
    private final CardSocketService cardSocketService;

    public void upgradeResearchTeam(String playerId, String cardId, Resources cost) {
        if (!gameStateService.gameIsInSession() || gameStateService.getGameState().getPhase() != Phase.Trade) {
            throw new UnsupportedOperationException("Cannot upgrade research team unless in trade phase");
        }
        if (!cardService.contains(cardId)) {
            throw new IllegalArgumentException("Card with id " + cardId + " does not exist");
        }
        if (!playerService.hasCardActive(playerId, cardId)) {
            throw new IllegalArgumentException("Player with id " + playerId + " does not own card with id " + cardId);
        }
        Card card = cardService.get(cardId);
        if (card.getType() != CardType.ResearchTeam) {
            throw new IllegalArgumentException("Card with id " + cardId + " is not a research team");
        }
        ResearchTeam researchTeam = (ResearchTeam) card;
        if (!cost.isAnOptionOf(researchTeam.getResearchOptions())) {
            throw new IllegalArgumentException(cost + " is an illegal option for researching " + researchTeam.getName());
        }
        Player player = playerService.get(playerId);
        Card newConverterCard = player.getInactiveCards().stream()
                .filter(c -> c.getType() == CardType.ConverterCard && researchTeam.getResultingTechnology().equals(c.getName()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("No converter card found for research team " + researchTeam.getName()));

        researchTeam.setResearched(true);
        cardSocketService.notifyClientOfUpdatedCard(researchTeam);
        int currentSharingBonus = gameStateService.getCurrentConfluence().getSharingBonus();
        Resources points = Resources.builder().points(currentSharingBonus + researchTeam.getPoints()).build();
        playerService.updatePlayerResources(playerId, cost, points, null);
        playerService.removeCardFromActive(playerId, cardId);
        playerService.acquireCardFromInactiveCards(playerId, newConverterCard.getId());
        gameStateService.addResearchedTechnology(researchTeam.getResultingTechnology());
    }

    public void upgradeColony(String playerId, String cardId) {
        if (!gameStateService.gameIsInSession() || gameStateService.getGameState().getPhase() != Phase.Trade) {
            throw new UnsupportedOperationException("Cannot upgrade colony unless in trade phase");
        }
        if (!playerService.hasCardActive(playerId, cardId)) {
            throw new IllegalArgumentException("Player with id " + playerId + " does not own card with id " + cardId);
        }
        Card card = cardService.get(cardId);
        if (card.getType() != CardType.Colony) {
            throw new IllegalArgumentException("Card with id " + cardId + " is not a colony");
        }
        Colony colony = (Colony) card;
        if (colony.isUpgraded()) {
            throw new IllegalArgumentException("Colony with id " + cardId + " is already upgraded");
        }
        Converter converter = colony.getUpgradeConverter();
        playerService.updatePlayerResources(playerId, converter.getInput(), converter.getOutput(), converter.getDonations());
        colony.setUpgraded(true);
        cardSocketService.notifyClientOfUpdatedCard(colony);
    }

    public void upgradeConverterCard(String playerId, String cardId, String technology) {
        if (!gameStateService.gameIsInSession() || gameStateService.getGameState().getPhase() != Phase.Trade) {
            throw new UnsupportedOperationException("Cannot upgrade converter card unless in trade phase");
        }
        if (!playerService.hasCardActive(playerId, cardId)) {
            throw new IllegalArgumentException("Player with id " + playerId + " does not own card with id " + cardId);
        }
        Card card = cardService.get(cardId);
        if (card.getType() != CardType.ConverterCard) {
            throw new IllegalArgumentException("Card with id " + cardId + " is not a converter card");
        }
        ConverterCard converterCard = (ConverterCard) card;
        if (converterCard.isUpgraded()) {
            throw new IllegalArgumentException("Converter card with id " + cardId + " is already upgraded");
        }
        Player player = playerService.get(playerId);
        Card technologyCard = player.getCards().stream()
                .filter(c -> c.getType() == CardType.ConverterCard && c.getName().equals(technology))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(player.getName() + " does not own converter card with name " + technology));
        playerService.removeCardFromActive(playerId, technologyCard.getId());
        converterCard.setUpgraded(true);
        cardSocketService.notifyClientOfUpdatedCard(converterCard);
    }

    public void acquireConfluenceCard(String playerId, String cardId) {
        GameState gameState = gameStateService.getGameState();
        if (!gameStateService.gameIsInSession() || gameState.getPhase() != Phase.Confluence) {
            throw new UnsupportedOperationException("Cannot acquire confluence card unless in confluence phase");
        }
        if (!cardService.contains(cardId)) {
            throw new IllegalArgumentException("Card with id " + cardId + " does not exist");
        }
        if (!gameState.getAvailableColonies().contains(cardId) && !gameState.getAvailableResearchTeams().contains(cardId)) {
            throw new IllegalArgumentException("Card with id " + cardId + " is not available in confluence bid track");
        }
        Card card = cardService.get(cardId);
        if (card.getType() != CardType.Colony && card.getType() != CardType.ResearchTeam) {
            throw new IllegalArgumentException("Card with id " + cardId + " is not a colony or research team");
        }
        if (!playerService.contains(playerId)) {
            throw new IllegalArgumentException("Player with id " + playerId + " does not exist");
        }
        gameStateService.removeConfluenceCard(cardId);
        playerService.acquireCard(playerId, cardId);
    }
}
