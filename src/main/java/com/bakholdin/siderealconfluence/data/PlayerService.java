package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.Race;
import com.bakholdin.siderealconfluence.model.RaceName;
import com.bakholdin.siderealconfluence.model.cards.Colony;
import com.bakholdin.siderealconfluence.model.cards.ConverterCard;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final CardService cardService;
    private final RaceService raceService;

    private final Map<UUID, Player> players = new HashMap<>();

    public Player createPlayer(String name, RaceName raceName) {
        Race race = raceService.initializeRaceForGame(raceName);
        List<ConverterCard> cards = cardService.fetchAndAddRaceConverterCardsToGame(raceName);
        Player newPlayer = Player.builder()
                .id(UUID.randomUUID())
                .name(name)
                .race(race)
                .resources(DataUtils.deepCopy(race.getStartingResources()))
                .cards(CardService.startingCards(cards))
                .availableCards(CardService.nonStartingCards(cards))
                .build();

        for (int i = 0; i < race.getStartingColonies(); i++) {
            Colony colony = cardService.drawColonyCard();
            newPlayer.getCards().add(colony.getId());
        }

        for (int i = 0; i < race.getStartingResearchTeams(); i++) {
            ResearchTeam researchTeam = cardService.drawResearchTeamCard();
            newPlayer.getCards().add(researchTeam.getId());
        }

        players.put(newPlayer.getId(), newPlayer);
        return newPlayer;
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
