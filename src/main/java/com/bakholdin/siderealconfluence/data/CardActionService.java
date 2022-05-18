package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.model.BidTrackType;
import com.bakholdin.siderealconfluence.model.Converter;
import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.model.Phase;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.PlayerBid;
import com.bakholdin.siderealconfluence.model.RaceName;
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
        if (player.getRace().getName() != RaceName.Yengii) {
            gameStateService.addResearchedTechnology(researchTeam.getResultingTechnology());
        }
    }

    public void leaseTechnologyToUser(String leasingPlayerId, String receivingPlayerId, String technologyName) {
        ValidationUtils.validatePlayerExists(playerService, leasingPlayerId);
        ValidationUtils.validatePlayerExists(playerService, receivingPlayerId);

        playerService.tryAcquireTechnology(receivingPlayerId, technologyName);
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
        Card card = cardService.get(cardId);
        ValidationUtils.validateConfluenceCardPresentInProperTrack(gameState, card);
        ValidationUtils.validatePlayerExists(playerService, playerId);
        Player player = playerService.get(playerId);
        PlayerBid playerBid = player.getPlayerBid();
        double bid = gameState.getActiveBidTrack() == BidTrackType.Colony ? playerBid.getColonyBid() : playerBid.getResearchTeamBid();
        if (player.getRace().getName() == RaceName.Caylion && gameState.getActiveBidTrack() == BidTrackType.Colony) {
            bid *= 2;
        }
        int shipCost = (int) Math.round(bid);
        Resources cost = Resources.builder().ships(shipCost).build();
        ValidationUtils.validatePlayerHasEnoughResources(player, cost);
        ValidationUtils.validatePlayerBidHighEnough(gameState, player, card);

        gameStateService.removeConfluenceCard(cardId);
        playerService.acquireCard(playerId, cardId);
        if (player.getRace().getName() == RaceName.Caylion && card.getType() == CardType.Colony) {
            Colony colony = (Colony) card;
            colony.setDoubledWithCaylion(true);
            cardSocketService.notifyClientOfUpdatedCard(colony);
        }
        playerService.updatePlayerResources(playerId, cost, null, null);
        gameStateService.advanceBids();
    }
}
