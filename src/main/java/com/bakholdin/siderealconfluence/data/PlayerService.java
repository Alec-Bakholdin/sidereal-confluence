package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.PlayerBid;
import com.bakholdin.siderealconfluence.model.Race;
import com.bakholdin.siderealconfluence.model.RaceName;
import com.bakholdin.siderealconfluence.model.Resources;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.CardType;
import com.bakholdin.siderealconfluence.service.PlayerSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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

    private final Map<UUID, Player> players = new HashMap<>();

    public Player createPlayer(String name, RaceName raceName) {
        Race race = raceService.initializeRaceForGame(raceName);
        Player newPlayer = Player.builder()
                .id(UUID.randomUUID())
                .name(name)
                .race(race)
                .build();

        players.put(newPlayer.getId(), newPlayer);
        return newPlayer;
    }

    public void resetPlayerWithoutSocket(UUID playerId) {
        ValidationUtils.validatePlayerExists(this, playerId);
        Player player = get(playerId);
        Race race = player.getRace();
        player.setPlayerBid(null);
        player.setReady(false);
        player.setInactiveCards(cardService.getInactiveCards(race));
        player.setCards(cardService.getStartingCards(player.getInactiveCards(), race));
        player.setResources(race.getStartingResources());
        player.setDonations(new Resources());
    }

    public void setReadyStatus(UUID playerId, boolean ready) {
        ValidationUtils.validatePlayerExists(this, playerId);
        Player player = get(playerId);
        player.setReady(ready);
        playerSocketService.notifyClientOfUpdatedReadyStatus(player);
    }

    public void setReadyStatus(String playerId, boolean ready) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        setReadyStatus(UUID.fromString(playerId), ready);
    }

    public void setPlayerBid(UUID playerId, int colonyBid, int researchTeamBid) {
        ValidationUtils.validatePlayerExists(this, playerId);
        Player player = get(playerId);
        PlayerBid playerBid = PlayerBid.builder()
                .playerId(player.getId())
                .colonyBid(colonyBid)
                .researchTeamBid(researchTeamBid)
                .build();
        ValidationUtils.validatePlayerHasEnoughShips(this, playerBid);
        player.setPlayerBid(playerBid);
    }

    public void setPlayerBid(String playerId, int colonyBid, int researchTeamBid) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        setPlayerBid(UUID.fromString(playerId), colonyBid, researchTeamBid);
    }

    public void setPlayerBid(UUID playerId, PlayerBid playerBid) {
        ValidationUtils.validatePlayerExists(this, playerId);
        get(playerId).setPlayerBid(playerBid);
    }

    public void setPlayerBid(String playerId, PlayerBid playerBid) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        setPlayerBid(UUID.fromString(playerId), playerBid);
    }

    public void acquireCardFromInactiveCards(UUID playerId, String cardId) {
        ValidationUtils.validateOwnsCard(this, playerId, cardId);
        if (hasCardActive(playerId, cardId)) {
            log.warn("Player {} already has active card {}", playerId, cardId);
            return;
        }
        Player player = get(playerId);
        Card card = cardService.get(cardId);
        player.getInactiveCards().remove(card);
        player.getCards().add(card);
        playerSocketService.notifyClientOfAcquiredCard(player, card);
    }

    public void acquireCardFromInactiveCards(String playerId, String cardId) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        acquireCardFromInactiveCards(UUID.fromString(playerId), cardId);
    }

    public void tryAcquireTechnology(UUID playerId, String technology) {
        ValidationUtils.validatePlayerExists(this, playerId);
        Player player = get(playerId);
        Card newConverterCard = player.getInactiveCards().stream()
                .filter(c -> c.getType() == CardType.ConverterCard && c.getName().equals(technology))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("No converter card found for technology " + technology));
        acquireCardFromInactiveCards(playerId, newConverterCard.getId());
    }

    public void acquireCard(UUID playerId, String cardId) {
        ValidationUtils.validatePlayerExists(this, playerId);
        if (ownsCard(playerId, cardId)) {
            log.warn("Player {} already has card {}", playerId, cardId);
            return;
        }
        Player player = get(playerId);
        Card card = cardService.get(cardId);
        player.getCards().add(card);
        playerSocketService.notifyClientOfAcquiredCard(player, card);
    }

    public void acquireCard(String playerId, String cardId) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        acquireCard(UUID.fromString(playerId), cardId);
    }

    public void removeCardFromActive(UUID playerId, String cardId) {
        ValidationUtils.validateOwnsCard(this, playerId, cardId);
        Player player = get(playerId);
        Card card = cardService.get(cardId);
        player.getCards().remove(card);
        playerSocketService.notifyClientOfRemovedActiveCard(player, card);
    }

    public void removeCardFromActive(String playerId, String cardId) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        removeCardFromActive(UUID.fromString(playerId), cardId);
    }

    public void updatePlayerResources(UUID playerId, Resources cost, Resources output, Resources donations) {
        ValidationUtils.validatePlayerExists(this, playerId);
        Player player = get(playerId);
        player.getResources().subtract(cost);
        player.getResources().add(output);
        player.getDonations().add(donations);
        playerSocketService.notifyClientOfUpdatedResources(player);
    }

    public void updatePlayerResources(String playerId, Resources cost, Resources output, Resources donations) {
        ValidationUtils.validateNonNullPlayerId(playerId);
        updatePlayerResources(UUID.fromString(playerId), cost, output, donations);
    }

    public void setPlayerResources(UUID playerId, Resources total, Resources donations) {
        ValidationUtils.validatePlayerExists(this, playerId);

        Player player = get(playerId);
        player.setResources(total);
        player.setDonations(donations);
        playerSocketService.notifyClientOfUpdatedResources(player);
    }

    public void setPlayerResources(String playerId, Resources resources, Resources donations) {
        ValidationUtils.validateNonNullPlayerId(playerId);

        setPlayerResources(UUID.fromString(playerId), resources, donations);
    }

    public void transferCard(UUID currentOwnerPlayerId, UUID newOwnerPlayerId, String cardId) {
        ValidationUtils.validatePlayerExists(this, currentOwnerPlayerId);
        ValidationUtils.validatePlayerExists(this, newOwnerPlayerId);
        ValidationUtils.validateCardIsActive(this, currentOwnerPlayerId, cardId);

        Player currentOwner = get(currentOwnerPlayerId);
        Player newOwner = get(newOwnerPlayerId);
        Card card = cardService.get(cardId);

        currentOwner.getCards().remove(card);
        newOwner.getCards().add(card);

        playerSocketService.notifyClientOfCardTransfer(currentOwner, newOwner, cardId);
        log.info("Transferred {} from {} to {}", card.getId(), currentOwner.getName(), newOwner.getName());
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
        return contains(playerId) && cardService.contains(cardId) && get(playerId).getInactiveCards().contains(cardService.get(cardId));
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

    public Player get(UUID id) {
        return players.get(id);
    }

    public Player get(String id) {
        return id == null ? null : get(UUID.fromString(id));
    }

    public boolean contains(UUID id) {
        return players.containsKey(id);
    }

    public boolean contains(String id) {
        return id != null && contains(UUID.fromString(id));
    }

}
