package com.bakholdin.siderealconfluence.data.cards;

import com.bakholdin.siderealconfluence.model.Race;
import com.bakholdin.siderealconfluence.model.RaceName;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.ConverterCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CardService {
    private final ColonyService colonyService;
    private final ConverterCardService converterCardService;
    private final ResearchTeamService researchTeamService;

    private Map<String, Card> gameCards = new HashMap<>();

    public static List<String> startingCards(List<ConverterCard> converterCards) {
        return converterCards.stream()
                .filter(ConverterCard::isStarting)
                .map(Card::getId)
                .collect(Collectors.toList());
    }

    public static List<String> nonStartingCards(List<ConverterCard> converterCards) {
        return converterCards.stream()
                .filter(converterCard -> !converterCard.isStarting())
                .map(Card::getId)
                .collect(Collectors.toList());
    }

    public String drawResearchTeam() {
        return researchTeamService.draw();
    }

    public List<String> drawResearchTeams(int numberOfTeams) {
        return researchTeamService.draw(numberOfTeams);
    }

    public String drawColony() {
        return colonyService.draw();
    }

    public List<String> drawColonies(int numberOfColonies) {
        return colonyService.draw(numberOfColonies);
    }


    private List<String> loadRaceConverterCards(RaceName raceName, boolean startingCards) {
        Map<String, ConverterCard> raceConverterCards = converterCardService.loadRaceConverterCards(raceName, startingCards);
        gameCards.putAll(raceConverterCards);
        return raceConverterCards.values().stream().map(Card::getId).collect(Collectors.toList());
    }

    public List<String> getStartingCards(Race race) {
        List<String> startingCards = loadRaceConverterCards(race.getName(), true);
        startingCards.addAll(drawColonies(race.getStartingColonies()));
        startingCards.addAll(drawResearchTeams(race.getStartingResearchTeams()));
        return startingCards;
    }

    public List<String> getAvailableCards(Race race) {
        return loadRaceConverterCards(race.getName(), false);
    }

    public Map<String, Card> resetCards() {
        gameCards = new HashMap<>();

        converterCardService.reset();
        gameCards.putAll(colonyService.reset());
        gameCards.putAll(researchTeamService.reset());

        return gameCards;
    }

    public Map<String, Card> getCurrentGameCards() {
        if (gameCards == null) {
            return resetCards();
        }
        return gameCards;
    }

    public Card get(String id) {
        return getCurrentGameCards().get(id);
    }

}
