package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.RaceName;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.Colony;
import com.bakholdin.siderealconfluence.model.cards.ConverterCard;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CardService {

    @Value(value = "classpath:game_data/cards/converterCards.json")
    private Resource converterCardsResource;
    @Value(value = "classpath:game_data/cards/researchTeams.json")
    private Resource researchTeamsResource;
    @Value(value = "classpath:game_data/cards/colonies.json")
    private Resource coloniesResource;

    private Map<String, Card> gameCards = new HashMap<>();
    private List<Colony> availableColonies = new ArrayList<>();
    private List<ResearchTeam> availableResearchTeams = new ArrayList<>();
    private Map<RaceName, Integer> numberOfRaceInstancesInGame = new HashMap<>();

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

    public Map<String, Card> resetCards() {
        gameCards = new HashMap<>();
        numberOfRaceInstancesInGame = new HashMap<>();
        loadResearchTeams();
        loadColonies();

        return gameCards;
    }

    public Map<String, Card> getCurrentGameCards() {
        if (gameCards == null) {
            return resetCards();
        }
        return gameCards;
    }

    public List<ConverterCard> fetchAndAddRaceConverterCardsToGame(RaceName race) {
        List<ConverterCard> raceConverters = loadConverterCardsForRace(race);

        // basically, when adding duplicate races, we need to add a suffix _1, _2, etc. to the end of the ids
        // so that if update/upgrades are made to one card, it only affects that individual card and
        // not all instances of the race
        int raceOccurrenceInGame = getNumberOfTimesRaceHasBeenAddedToGame(race);
        String occurrenceSuffix = raceOccurrenceInGame == 0 ? "" : String.format("_%s", raceOccurrenceInGame);
        raceConverters.forEach(card -> card.setId(card.getId() + occurrenceSuffix));

        addRaceConverterListToGame(raceConverters, raceOccurrenceInGame);
        return raceConverters;
    }

    private int getNumberOfTimesRaceHasBeenAddedToGame(RaceName race) {
        if (!numberOfRaceInstancesInGame.containsKey(race)) {
            numberOfRaceInstancesInGame.put(race, 0);
        }
        int raceOccurrenceInGame = numberOfRaceInstancesInGame.get(race);
        numberOfRaceInstancesInGame.replace(race, raceOccurrenceInGame + 1);
        return raceOccurrenceInGame;
    }

    private void addRaceConverterListToGame(List<ConverterCard> raceConverters, int raceOccurrenceInGame) {
        Map<String, Card> raceConverterMap = raceConverters.stream()
                .collect(Collectors.toMap(Card::getId, card -> card));
        gameCards.putAll(raceConverterMap);
    }

    public Card get(String id) {
        return getCurrentGameCards().get(id);
    }

    public Colony drawColonyCard() {
        if (availableColonies.isEmpty()) {
            throw new RuntimeException("No more colonies available");
        }
        return availableColonies.remove(0);
    }

    public List<Colony> drawNColonies(int n) {
        List<Colony> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(drawColonyCard());
        }
        return result;
    }

    public ResearchTeam drawResearchTeamCard() {
        if (availableResearchTeams.isEmpty()) {
            throw new RuntimeException("No more research teams available");
        }
        return availableResearchTeams.remove(0);
    }

    public List<ResearchTeam> drawNResearchTeams(int n) {
        List<ResearchTeam> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(drawResearchTeamCard());
        }
        return result;
    }

    private void loadColonies() {
        availableColonies = DataUtils.loadListFromResource(coloniesResource, new TypeReference<>() {
        });
        Map<String, Colony> colonyMap = availableColonies.stream().collect(Collectors.toMap(Card::getId, card -> card));
        gameCards.putAll(colonyMap);
        Collections.shuffle(availableColonies);
        log.info("Loaded {} colonies", availableColonies.size());
    }

    private void loadResearchTeams() {
        availableResearchTeams = DataUtils.loadListFromResource(researchTeamsResource, new TypeReference<>() {
        });
        Map<String, ResearchTeam> researchTeamMap = availableResearchTeams.stream().collect(Collectors.toMap(Card::getId, card -> card));
        gameCards.putAll(researchTeamMap);

        // order randomly within each era
        Collections.shuffle(availableResearchTeams);
        availableResearchTeams.sort(Comparator.comparingInt(ResearchTeam::getEra));
        log.info("Loaded {} research teams", availableResearchTeams.size());
    }

    private List<ConverterCard> loadConverterCardsForRace(RaceName race) {
        List<ConverterCard> converterCardList = DataUtils.loadListFromResource(converterCardsResource, new TypeReference<>() {
        });
        List<ConverterCard> converterCardWithRace = converterCardList.stream().filter(card -> card.getRace().equals(race)).collect(Collectors.toList());
        log.info("Loaded {} converter cards for {}", converterCardWithRace.size(), race);
        return converterCardWithRace;
    }


}
