package com.bakholdin.siderealconfluence.data.cards;

import com.bakholdin.siderealconfluence.model.Race;
import com.bakholdin.siderealconfluence.model.RaceName;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.CardType;
import com.bakholdin.siderealconfluence.model.cards.Colony;
import com.bakholdin.siderealconfluence.model.cards.ConverterCard;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam;
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

    private List<Card> loadRaceConverterCards(RaceName raceName, boolean startingCards) {
        Map<String, ConverterCard> raceConverterCards = converterCardCardService.loadRaceConverterCards(raceName, startingCards);
        gameCards.putAll(raceConverterCards);
        return raceConverterCards.values().stream().map(card -> (Card) card).collect(Collectors.toList());
    }

    public List<Colony> drawColonies(int n) {
        return colonyCardService.draw(n);
    }

    public List<ResearchTeam> drawResearchTeams(int n) {
        return researchTeamService.draw(n);
    }

    public List<Card> draw(int n, CardType cardType) {
        switch (cardType) {
            case Colony:
                return drawColonies(n).stream().map(card -> (Card) card).collect(Collectors.toList());
            case ResearchTeam:
                return researchTeamService.draw(n).stream().map(card -> (Card) card).collect(Collectors.toList());
            case ConverterCard:
            default:
                throw new IllegalArgumentException("Invalid card type to draw: " + cardType);
        }
    }

    public List<String> drawIds(int n, CardType cardType) {
        return draw(n, cardType).stream().map(Card::getId).collect(Collectors.toList());
    }

    public List<Card> getStartingCards(Race race) {
        List<Card> startingCards = loadRaceConverterCards(race.getName(), true);
        startingCards.addAll(draw(race.getStartingColonies(), CardType.Colony));
        startingCards.addAll(draw(race.getStartingResearchTeams(), CardType.ResearchTeam));
        return startingCards;
    }

    public List<Card> getInactiveCards(Race race) {
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

    public boolean contains(String id) {
        return getCurrentGameCards().containsKey(id);
    }

}
