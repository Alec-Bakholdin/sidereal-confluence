package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.model.Player1;
import com.bakholdin.siderealconfluence.model.PlayerBid1;
import com.bakholdin.siderealconfluence.model.Race1;
import com.bakholdin.siderealconfluence.model.RaceName1;
import com.bakholdin.siderealconfluence.model.Resources1;
import com.bakholdin.siderealconfluence.model.cards.Card1;
import com.bakholdin.siderealconfluence.model.cards.CardType1;
import com.bakholdin.siderealconfluence.service.PlayerSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class PlayerService {
    private final CardService cardService;
    private final RaceService raceService;
    private final PlayerSocketService playerSocketService;

    private final Map<UUID, Player1> players = new HashMap<>();

    public Player1 createPlayer(String name, RaceName1 raceName) {
        Race1 race = raceService.initializeRaceForGame(raceName);
        Player1 newPlayer = Player1.builder()
                .id(UUID.randomUUID())
                .name(name)
                .race(race)
                .build();

        players.put(newPlayer.getId(), newPlayer);
        return newPlayer;
    }

    public void resetPlayerWithoutSocket(UUID playerId) {
        ValidationUtils.validatePlayerExists(this, playerId);
        Player1 player = get(playerId);
        Race1 race = player.getRace();
        player.setPlayerBid(null);
        player.setReady(false);
        player.setInactiveCard1s(cardService.getInactiveCards(race));
        player.setCard1s(cardService.getStartingCards(player.getInactiveCard1s(), race));
        player.setResearchedTechnologies(new ArrayList<>());
        player.setResources(DataUtils.deepCopy(race.getStartingResources()));
        player.setDonations(new Resources1());
    }

    public void setReadyStatus(UUID playerId, boolean ready) {
        ValidationUtils.validatePlayerExists(this, playerId);
        Player1 player = get(playerId);
        player.setReady(ready);
        playerSocketService.notifyClientOfUpdatedReadyStatus(player);
    }

    public void setReadyStatus(String playerId, boolean ready) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        setReadyStatus(UUID.fromString(playerId), ready);
    }

    public void setPlayerBid(UUID playerId, double colonyBid, double researchTeamBid) {
        ValidationUtils.validatePlayerExists(this, playerId);
        Player1 player = get(playerId);
        if (player.getRace().getName() == RaceName1.Caylion) {
            colonyBid /= 2;
        }
        PlayerBid1 playerBid = PlayerBid1.builder()
                .playerId(player.getId())
                .colonyBid(colonyBid)
                .researchTeamBid(researchTeamBid)
                .build();
        ValidationUtils.validatePlayerHasEnoughShips(this, playerBid);
        player.setPlayerBid(playerBid);
    }

    public void setPlayerBid(String playerId, double colonyBid, double researchTeamBid) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        setPlayerBid(UUID.fromString(playerId), colonyBid, researchTeamBid);
    }

    public void setPlayerBid(UUID playerId, PlayerBid1 playerBid) {
        ValidationUtils.validatePlayerExists(this, playerId);
        get(playerId).setPlayerBid(playerBid);
    }

    public void setPlayerBid(String playerId, PlayerBid1 playerBid) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        setPlayerBid(UUID.fromString(playerId), playerBid);
    }

    public void acquireCardFromInactiveCards(UUID playerId, String cardId) {
        ValidationUtils.validateOwnsCard(this, playerId, cardId);
        if (hasCardActive(playerId, cardId)) {
            log.warn("Player {} already has active card {}", playerId, cardId);
            return;
        }
        Player1 player = get(playerId);
        Card1 card1 = cardService.get(cardId);
        player.getInactiveCard1s().remove(card1);
        player.getCard1s().add(card1);
        playerSocketService.notifyClientOfAcquiredCard(player, card1);
    }

    public void acquireCardFromInactiveCards(String playerId, String cardId) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        acquireCardFromInactiveCards(UUID.fromString(playerId), cardId);
    }

    public void tryAcquireTechnology(UUID playerId, String technology) {
        Player1 player = get(playerId);
        Card1 newConverterCard1 = player.getInactiveCard1s().stream()
                .filter(c -> c.getType() == CardType1.ConverterCard && c.getName().equals(technology))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("No converter card found for technology " + technology));
        acquireCardFromInactiveCards(playerId, newConverterCard1.getId());
    }

    public void tryAcquireTechnology(String playerId, String technology) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        tryAcquireTechnology(UUID.fromString(playerId), technology);
    }

    public void addResearchedTechnology(UUID playerId, String technology) {
        Player1 player = get(playerId);
        player.getResearchedTechnologies().add(technology);
    }

    public void addResearchedTechnology(String playerId, String technology) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        addResearchedTechnology(UUID.fromString(playerId), technology);
    }

    public void acquireCard(UUID playerId, String cardId) {
        ValidationUtils.validatePlayerExists(this, playerId);
        if (ownsCard(playerId, cardId)) {
            log.warn("Player {} already has card {}", playerId, cardId);
            return;
        }
        Player1 player = get(playerId);
        Card1 card1 = cardService.get(cardId);
        player.getCard1s().add(card1);
        playerSocketService.notifyClientOfAcquiredCard(player, card1);
    }

    public void acquireCard(String playerId, String cardId) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        acquireCard(UUID.fromString(playerId), cardId);
    }

    public void removeCardFromActive(UUID playerId, String cardId) {
        ValidationUtils.validateOwnsCard(this, playerId, cardId);
        Player1 player = get(playerId);
        Card1 card1 = cardService.get(cardId);
        player.getCard1s().remove(card1);
        playerSocketService.notifyClientOfRemovedActiveCard(player, card1);
    }

    public void removeCardFromActive(String playerId, String cardId) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        removeCardFromActive(UUID.fromString(playerId), cardId);
    }

    public void updatePlayerResources(UUID playerId, Resources1 cost, Resources1 output, Resources1 donations) {
        ValidationUtils.validatePlayerExists(this, playerId);
        Player1 player = get(playerId);
        player.getResources().subtract(cost);
        player.getResources().add(output);
        player.getDonations().add(donations);
        playerSocketService.notifyClientOfUpdatedResources(player);
    }

    public void updatePlayerResources(String playerId, Resources1 cost, Resources1 output, Resources1 donations) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        updatePlayerResources(UUID.fromString(playerId), cost, output, donations);
    }

    public void setPlayerResources(UUID playerId, Resources1 total, Resources1 donations) {
        ValidationUtils.validatePlayerExists(this, playerId);

        Player1 player = get(playerId);
        player.setResources(total);
        player.setDonations(donations);
        playerSocketService.notifyClientOfUpdatedResources(player);
    }

    public void setPlayerResources(String playerId, Resources1 resources, Resources1 donations) {
        ValidationUtils.validateNonNullPlayerId(playerId);

        setPlayerResources(UUID.fromString(playerId), resources, donations);
    }

    public void transferCard(UUID currentOwnerPlayerId, UUID newOwnerPlayerId, String cardId) {
        ValidationUtils.validatePlayerExists(this, currentOwnerPlayerId);
        ValidationUtils.validatePlayerExists(this, newOwnerPlayerId);
        ValidationUtils.validateCardIsActive(this, currentOwnerPlayerId, cardId);

        Player1 currentOwner = get(currentOwnerPlayerId);
        Player1 newOwner = get(newOwnerPlayerId);
        Card1 card1 = cardService.get(cardId);

        currentOwner.getCard1s().remove(card1);
        newOwner.getCard1s().add(card1);

        playerSocketService.notifyClientOfCardTransfer(currentOwner, newOwner, cardId);
        log.info("Transferred {} from {} to {}", card1.getId(), currentOwner.getName(), newOwner.getName());
    }

    public void transferCard(String currentOwnerPlayerId, String newOwnerPlayerId, String cardId) {
        ValidationUtils.validateNonNullPlayerId(currentOwnerPlayerId);
        ValidationUtils.validateNonNullPlayerId(newOwnerPlayerId);
        transferCard(UUID.fromString(currentOwnerPlayerId), UUID.fromString(newOwnerPlayerId), cardId);
    }

    public boolean ownsCard(UUID playerId, String cardId) {
        return hasCardActive(playerId, cardId) || hasCardInactive(playerId, cardId);
    }

    public boolean ownsCard(String playerId, String cardId) {
        return playerId != null && ownsCard(UUID.fromString(playerId), cardId);
    }

    public boolean hasCardInactive(UUID playerId, String cardId) {
        return contains(playerId) && cardService.contains(cardId) && get(playerId).getInactiveCard1s().contains(cardService.get(cardId));
    }

    public boolean hasCardInactive(String playerId, String cardId) {
        return playerId != null && hasCardInactive(UUID.fromString(playerId), cardId);
    }

    public boolean hasCardActive(UUID playerId, String cardId) {
        return contains(playerId) && cardService.contains(cardId) && get(playerId).cardIds().contains(cardId);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasCardActive(String playerId, String cardId) {
        return playerId != null && hasCardActive(UUID.fromString(playerId), cardId);
    }

    public void resetPlayers() {
        players.clear();
    }

    public Player1 get(UUID id) {
        ValidationUtils.validatePlayerExists(this, id);
        return players.get(id);
    }

    public Player1 get(String id) {
        return id == null ? null : get(UUID.fromString(id));
    }

    public boolean contains(UUID id) {
        return players.containsKey(id);
    }

    public boolean contains(String id) {
        return id != null && contains(UUID.fromString(id));
    }

}
