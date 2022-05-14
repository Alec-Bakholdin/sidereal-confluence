package com.bakholdin.siderealconfluence.data.cards;

import com.bakholdin.siderealconfluence.model.Race;
import com.bakholdin.siderealconfluence.model.RaceName;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.CardType;
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
    private final ColonyCardService colonyCardService;
    private final ConverterCardCardService converterCardCardService;
    private final ResearchTeamCardService researchTeamService;

    private Map<String, Card> gameCards = new HashMap<>();

    private List<String> loadRaceConverterCards(RaceName raceName, boolean startingCards) {
        Map<String, ConverterCard> raceConverterCards = converterCardCardService.loadRaceConverterCards(raceName, startingCards);
        gameCards.putAll(raceConverterCards);
        return raceConverterCards.values().stream().map(Card::getId).collect(Collectors.toList());
    }

    public List<String> draw(int n, CardType cardType) {
        switch (cardType) {
            case Colony:
                return colonyCardService.draw(n);
            case ResearchTeam:
                return researchTeamService.draw(n);
            case ConverterCard:
            default:
                throw new IllegalArgumentException("Invalid card type to draw: " + cardType);
        }
    }

    public List<String> getStartingCards(Race race) {
        List<String> startingCards = loadRaceConverterCards(race.getName(), true);
        startingCards.addAll(draw(race.getStartingColonies(), CardType.Colony));
        startingCards.addAll(draw(race.getStartingResearchTeams(), CardType.ResearchTeam));
        return startingCards;
    }

    public List<String> getAvailableCards(Race race) {
        return loadRaceConverterCards(race.getName(), false);
    }

    public Map<String, Card> resetCards() {
        gameCards = new HashMap<>();

        converterCardCardService.reset();
        gameCards.putAll(colonyCardService.reset());
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
