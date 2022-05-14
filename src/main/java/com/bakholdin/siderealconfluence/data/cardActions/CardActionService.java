package com.bakholdin.siderealconfluence.data.cardActions;

import com.bakholdin.siderealconfluence.data.GameStateService;
import com.bakholdin.siderealconfluence.data.PlayerService;
import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.model.Phase;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.Resources;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.CardType;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam;
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
        researchTeam.setResearched(true);
        Card newConverterCard = player.getInactiveCards().stream()
                .filter(c -> c.getType() == CardType.ConverterCard && researchTeam.getResultingTechnology().equals(c.getName()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("No converter card found for research team " + researchTeam.getName()));
        playerService.updatePlayerResources(playerId, cost, new Resources());
        playerService.removeCardFromActive(playerId, cardId);
        playerService.acquireCard(playerId, newConverterCard.getId());
        gameStateService.addResearchedTechnology(researchTeam.getResultingTechnology());
    }
}
