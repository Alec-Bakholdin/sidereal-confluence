package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.Race;
import com.bakholdin.siderealconfluence.model.RaceName;
import com.bakholdin.siderealconfluence.model.Resources;
import com.bakholdin.siderealconfluence.model.cards.Card;
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
                .resources(DataUtils.deepCopy(race.getStartingResources()))
                .cards(cardService.getStartingCards(race))
                .inactiveCards(cardService.getInactiveCards(race))
                .build();

        players.put(newPlayer.getId(), newPlayer);
        return newPlayer;
    }

    public void acquireCardFromInactiveCards(UUID playerId, String cardId) {
        if (!ownsCard(playerId, cardId)) {
            throw new IllegalArgumentException("Player does not own card");
        }
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
        if (playerId == null) {
            throw new IllegalArgumentException("Player id cannot be null");
        }
        acquireCardFromInactiveCards(UUID.fromString(playerId), cardId);
    }

    public void acquireCard(UUID playerId, String cardId) {
        if (!contains(playerId)) {
            throw new IllegalArgumentException("Player does not exist");
        }
        if (ownsCard(playerId, cardId)) {
            log.warn("Player {} already has card {}", playerId, cardId);
        }
    }

    public void acquireCard(String playerId, String cardId) {

    }

    public void removeCardFromActive(UUID playerId, String cardId) {
        if (!ownsCard(playerId, cardId)) {
            throw new IllegalArgumentException("Player does not own card");
        }
        Player player = get(playerId);
        Card card = cardService.get(cardId);
        player.getCards().remove(card);
        playerSocketService.notifyClientOfRemovedActiveCard(player, card);
    }

    public void removeCardFromActive(String playerId, String cardId) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player id cannot be null");
        }
        removeCardFromActive(UUID.fromString(playerId), cardId);
    }

    public void updatePlayerResources(UUID playerId, Resources cost, Resources output) {
        if (!contains(playerId)) {
            return;
        }
        Player player = get(playerId);
        player.getResources().subtract(cost);
        player.getResources().add(output);
        playerSocketService.notifyClientOfUpdatedResources(player);
    }

    public void updatePlayerResources(String playerId, Resources cost, Resources output) {
        if (playerId == null) {
            return;
        }
        updatePlayerResources(UUID.fromString(playerId), cost, output);
    }

    public void transferCard(String currentOwnerPlayerId, String newOwnerPlayerId, String cardId) {
        if (currentOwnerPlayerId == null || newOwnerPlayerId == null || cardId == null) {
            throw new IllegalArgumentException("PlayerService.transferCard: null argument");
        }
        if (!contains(currentOwnerPlayerId) || !contains(newOwnerPlayerId)) {
            throw new IllegalArgumentException("PlayerId does not exist");
        }
        if (!hasCardActive(currentOwnerPlayerId, cardId)) {
            throw new IllegalArgumentException("Current owner does not own card with id " + cardId);
        }
        Player currentOwner = get(currentOwnerPlayerId);
        Player newOwner = get(newOwnerPlayerId);
        Card card = cardService.get(cardId);

        currentOwner.getCards().remove(card);
        newOwner.getCards().add(card);

        playerSocketService.notifyClientOfCardTransfer(currentOwner, newOwner, cardId);
        log.info("Transferred {} from {} to {}", card.getId(), currentOwner.getName(), newOwner.getName());
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
