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

    //public void addResearchTechFromAvailableCards

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
        if (!cardService.contains(cardId)) {
            throw new IllegalArgumentException("CardId does not exist");
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
        return contains(playerId) && get(playerId).cardIds().contains(cardId);
    }

    public boolean ownsCard(String playerId, String cardId) {
        return playerId != null && ownsCard(UUID.fromString(playerId), cardId);
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
