package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.old_model.BidTrackType1;
import com.bakholdin.siderealconfluence.old_model.Converter1;
import com.bakholdin.siderealconfluence.old_model.GameState1;
import com.bakholdin.siderealconfluence.old_model.Phase1;
import com.bakholdin.siderealconfluence.old_model.Player1;
import com.bakholdin.siderealconfluence.old_model.PlayerBid1;
import com.bakholdin.siderealconfluence.old_model.RaceName1;
import com.bakholdin.siderealconfluence.old_model.Resources1;
import com.bakholdin.siderealconfluence.old_model.cards.Card1;
import com.bakholdin.siderealconfluence.old_model.cards.CardType1;
import com.bakholdin.siderealconfluence.old_model.cards.Colony1;
import com.bakholdin.siderealconfluence.old_model.cards.ConverterCard1;
import com.bakholdin.siderealconfluence.old_model.cards.ResearchTeam1;
import com.bakholdin.siderealconfluence.old_service.CardSocketService;
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

    public void upgradeResearchTeam(String playerId, String cardId, Resources1 cost) {
        ValidationUtils.validatePhase(gameStateService, Phase1.Trade);
        ValidationUtils.validateCardExists(cardService, cardId);
        ValidationUtils.validateCardIsActive(playerService, playerId, cardId);
        Card1 card1 = cardService.get(cardId);
        ValidationUtils.validateCardType(CardType1.ResearchTeam, card1);
        ResearchTeam1 researchTeam1 = (ResearchTeam1) card1;
        if (!cost.isAnOptionOf(researchTeam1.getResearchOptions())) {
            throw new IllegalArgumentException(cost + " is an illegal option for researching " + researchTeam1.getName());
        }


        // get resulting technology card
        Player1 player = playerService.get(playerId);
        playerService.tryAcquireTechnology(player.getId(), researchTeam1.getResultingTechnology());

        // get points, pay costs, and remove card from player's hand
        researchTeam1.setResearched(true);
        cardSocketService.notifyClientOfUpdatedCard(researchTeam1);
        int currentSharingBonus = gameStateService.getCurrentConfluence().getSharingBonus();
        Resources1 points = Resources1.builder().points(currentSharingBonus + researchTeam1.getPoints()).build();
        playerService.updatePlayerResources(playerId, cost, points, null);
        playerService.removeCardFromActive(playerId, cardId);

        // add researched technology to pending researches
        if (player.getRace().getName() != RaceName1.Yengii) {
            gameStateService.addResearchedTechnology(researchTeam1.getResultingTechnology());
        }
    }

    public void leaseTechnologyToUser(String leasingPlayerId, String receivingPlayerId, String technologyName) {
        ValidationUtils.validatePlayerExists(playerService, leasingPlayerId);
        ValidationUtils.validatePlayerExists(playerService, receivingPlayerId);

        playerService.tryAcquireTechnology(receivingPlayerId, technologyName);
    }

    public void upgradeColony(String playerId, String cardId) {
        ValidationUtils.validatePhase(gameStateService, Phase1.Trade);
        ValidationUtils.validateCardExists(cardService, cardId);
        ValidationUtils.validateCardIsActive(playerService, playerId, cardId);
        Card1 card1 = cardService.get(cardId);
        ValidationUtils.validateCardType(CardType1.Colony, card1);
        Colony1 colony1 = (Colony1) card1;
        if (colony1.isUpgraded()) {
            throw new IllegalArgumentException("Colony with id " + cardId + " is already upgraded");
        }

        // upgrade colony and pay costs
        Converter1 converter1 = colony1.getUpgradeConverter1();
        playerService.updatePlayerResources(playerId, converter1.getInput(), converter1.getOutput(), converter1.getDonations());
        colony1.setUpgraded(true);
        cardSocketService.notifyClientOfUpdatedCard(colony1);
    }

    public void upgradeConverterCard(String playerId, String cardId, String technology) {
        ValidationUtils.validatePhase(gameStateService, Phase1.Trade);
        ValidationUtils.validateCardExists(cardService, cardId);
        ValidationUtils.validateCardIsActive(playerService, playerId, cardId);
        Card1 card1 = cardService.get(cardId);
        ValidationUtils.validateCardType(CardType1.ConverterCard, card1);
        ConverterCard1 converterCard = (ConverterCard1) card1;
        if (converterCard.isUpgraded()) {
            throw new IllegalArgumentException("Converter card with id " + cardId + " is already upgraded");
        }

        // upgrade converter and remove the card that's consumed for the upgrade
        Player1 player = playerService.get(playerId);
        Card1 technologyCard1 = player.getCard1s().stream()
                .filter(c -> c.getType() == CardType1.ConverterCard && c.getName().equals(technology))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(player.getName() + " does not own converter card with name " + technology));
        playerService.removeCardFromActive(playerId, technologyCard1.getId());
        converterCard.setUpgraded(true);
        cardSocketService.notifyClientOfUpdatedCard(converterCard);
    }

    public void acquireConfluenceCard(String playerId, String cardId) {
        ValidationUtils.validatePhase(gameStateService, Phase1.Confluence);
        ValidationUtils.validateCardExists(cardService, cardId);
        GameState1 gameState = gameStateService.getGameState();
        Card1 card1 = cardService.get(cardId);
        ValidationUtils.validateConfluenceCardPresentInProperTrack(gameState, card1);
        ValidationUtils.validatePlayerExists(playerService, playerId);
        Player1 player = playerService.get(playerId);
        PlayerBid1 playerBid = player.getPlayerBid();
        double bid = gameState.getActiveBidTrack() == BidTrackType1.Colony ? playerBid.getColonyBid() : playerBid.getResearchTeamBid();
        if (player.getRace().getName() == RaceName1.Caylion && gameState.getActiveBidTrack() == BidTrackType1.Colony) {
            bid *= 2;
        }
        int shipCost = (int) Math.round(bid);
        Resources1 cost = Resources1.builder().ships(shipCost).build();
        ValidationUtils.validatePlayerHasEnoughResources(player, cost);
        ValidationUtils.validatePlayerBidHighEnough(gameState, player, card1);

        gameStateService.removeConfluenceCard(cardId);
        playerService.acquireCard(playerId, cardId);
        if (player.getRace().getName() == RaceName1.Caylion && card1.getType() == CardType1.Colony) {
            Colony1 colony1 = (Colony1) card1;
            colony1.setDoubledWithCaylion(true);
            cardSocketService.notifyClientOfUpdatedCard(colony1);
        }
        playerService.updatePlayerResources(playerId, cost, null, null);
        gameStateService.advanceBids();
    }
}
