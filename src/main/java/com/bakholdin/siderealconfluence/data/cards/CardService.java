package com.bakholdin.siderealconfluence.data.cards;

import com.bakholdin.siderealconfluence.model.Race1;
import com.bakholdin.siderealconfluence.model.RaceName1;
import com.bakholdin.siderealconfluence.model.cards.Card1;
import com.bakholdin.siderealconfluence.model.cards.CardType1;
import com.bakholdin.siderealconfluence.model.cards.Colony1;
import com.bakholdin.siderealconfluence.model.cards.ConverterCard1;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam1;
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

    private Map<String, Card1> gameCards = new HashMap<>();

    private List<Card1> loadRaceConverterCards(RaceName1 raceName) {
        Map<String, ConverterCard1> raceConverterCards = converterCardCardService.loadRaceConverterCards(raceName);
        gameCards.putAll(raceConverterCards);
        return raceConverterCards.values().stream().map(card -> (Card1) card).collect(Collectors.toList());
    }

    public List<Colony1> drawColonies(int n) {
        return colonyCardService.draw(n);
    }

    public List<ResearchTeam1> drawResearchTeams(int n) {
        return researchTeamService.draw(n);
    }

    public List<Card1> draw(int n, CardType1 cardType1) {
        switch (cardType1) {
            case Colony:
                return drawColonies(n).stream().map(card -> (Card1) card).collect(Collectors.toList());
            case ResearchTeam:
                return researchTeamService.draw(n).stream().map(card -> (Card1) card).collect(Collectors.toList());
            case ConverterCard:
            default:
                throw new IllegalArgumentException("Invalid card type to draw: " + cardType1);
        }
    }

    public List<String> drawIds(int n, CardType1 cardType1) {
        return draw(n, cardType1).stream().map(Card1::getId).collect(Collectors.toList());
    }

    public List<Card1> getStartingCards(List<Card1> inactiveCard1s, Race1 race) {
        List<Card1> startingCard1s = inactiveCard1s.stream()
                .filter(card -> card.getType() == CardType1.ConverterCard && ((ConverterCard1) card).isStarting())
                .collect(Collectors.toList());
        List<Colony1> startingColonies = colonyCardService.draw(race.getStartingColonies());
        if (race.getName() == RaceName1.Caylion) {
            startingColonies.forEach(colony -> colony.setDoubledWithCaylion(true));
        }
        startingCard1s.addAll(startingColonies);
        startingCard1s.addAll(draw(race.getStartingResearchTeams(), CardType1.ResearchTeam));
        return startingCard1s;
    }

    public List<Card1> getInactiveCards(Race1 race) {
        return loadRaceConverterCards(race.getName());
    }

    public Map<String, Card1> resetCards() {
        gameCards = new HashMap<>();

        converterCardCardService.reset();
        gameCards.putAll(colonyCardService.reset());
        gameCards.putAll(researchTeamService.reset());

        return gameCards;
    }

    public Map<String, Card1> getCurrentGameCards() {
        if (gameCards == null) {
            return resetCards();
        }
        return gameCards;
    }

    public Card1 get(String id) {
        return getCurrentGameCards().get(id);
    }

    public boolean contains(String id) {
        return getCurrentGameCards().containsKey(id);
    }

}
