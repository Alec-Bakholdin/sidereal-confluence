package com.bakholdin.siderealconfluence.data;

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
        ValidationUtils.validatePhase(gameStateService, Phase.Trade);
        ValidationUtils.validateCardExists(cardService, cardId);
        ValidationUtils.validateCardIsActive(playerService, playerId, cardId);
        Card card = cardService.get(cardId);
        ValidationUtils.validateCardType(CardType.ResearchTeam, card);
        ResearchTeam researchTeam = (ResearchTeam) card;
        if (!cost.isAnOptionOf(researchTeam.getResearchOptions())) {
            throw new IllegalArgumentException(cost + " is an illegal option for researching " + researchTeam.getName());
        }


        // get resulting technology card
        Player player = playerService.get(playerId);
        playerService.tryAcquireTechnology(player.getId(), researchTeam.getResultingTechnology());

        // get points, pay costs, and remove card from player's hand
        researchTeam.setResearched(true);
        cardSocketService.notifyClientOfUpdatedCard(researchTeam);
        int currentSharingBonus = gameStateService.getCurrentConfluence().getSharingBonus();
        Resources points = Resources.builder().points(currentSharingBonus + researchTeam.getPoints()).build();
        playerService.updatePlayerResources(playerId, cost, points, null);
        playerService.removeCardFromActive(playerId, cardId);

        // add researched technology to pending researches
        gameStateService.addResearchedTechnology(researchTeam.getResultingTechnology());
    }

    public void upgradeColony(String playerId, String cardId) {
        ValidationUtils.validatePhase(gameStateService, Phase.Trade);
        ValidationUtils.validateCardExists(cardService, cardId);
        ValidationUtils.validateCardIsActive(playerService, playerId, cardId);
        Card card = cardService.get(cardId);
        ValidationUtils.validateCardType(CardType.Colony, card);
        Colony colony = (Colony) card;
        if (colony.isUpgraded()) {
            throw new IllegalArgumentException("Colony with id " + cardId + " is already upgraded");
        }

        // upgrade colony and pay costs
        Converter converter = colony.getUpgradeConverter();
        playerService.updatePlayerResources(playerId, converter.getInput(), converter.getOutput(), converter.getDonations());
        colony.setUpgraded(true);
        cardSocketService.notifyClientOfUpdatedCard(colony);
    }

    public void upgradeConverterCard(String playerId, String cardId, String technology) {
        ValidationUtils.validatePhase(gameStateService, Phase.Trade);
        ValidationUtils.validateCardExists(cardService, cardId);
        ValidationUtils.validateCardIsActive(playerService, playerId, cardId);
        Card card = cardService.get(cardId);
        ValidationUtils.validateCardType(CardType.ConverterCard, card);
        ConverterCard converterCard = (ConverterCard) card;
        if (converterCard.isUpgraded()) {
            throw new IllegalArgumentException("Converter card with id " + cardId + " is already upgraded");
        }

        // upgrade converter and remove the card that's consumed for the upgrade
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
        ValidationUtils.validatePhase(gameStateService, Phase.Confluence);
        ValidationUtils.validateCardExists(cardService, cardId);
        GameState gameState = gameStateService.getGameState();
        if (!gameState.getAvailableColonies().contains(cardId) && !gameState.getAvailableResearchTeams().contains(cardId)) {
            throw new IllegalArgumentException("Card with id " + cardId + " is not available in confluence bid track");
        }
        Card card = cardService.get(cardId);
        ValidationUtils.validateCardType(CardType.Colony, CardType.ResearchTeam, card);
        ValidationUtils.validatePlayerExists(playerService, playerId);

        gameStateService.removeConfluenceCard(cardId);
        playerService.acquireCard(playerId, cardId);
    }
}
