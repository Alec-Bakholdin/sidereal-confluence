package com.bakholdin.siderealconfluence.data.cards;

import com.bakholdin.siderealconfluence.data.DataUtils;
import com.bakholdin.siderealconfluence.model.RaceName1;
import com.bakholdin.siderealconfluence.model.cards.Card1;
import com.bakholdin.siderealconfluence.model.cards.ConverterCard1;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ConverterCardCardService {
    private final Map<RaceName1, Integer> numberOfRaceInstancesInGame = new HashMap<>();

    @Value(value = "classpath:game_data/cards/converterCards.json")
    private Resource converterCardsResource;

    protected void reset() {
        numberOfRaceInstancesInGame.clear();
    }

    protected Map<String, ConverterCard1> loadRaceConverterCards(RaceName1 race) {
        List<ConverterCard1> raceConverters = loadConverterCardsForRace(race);

        // basically, when adding duplicate races, we need to add a suffix _1, _2, etc. to the end of the ids
        // so that if update/upgrades are made to one card, it only affects that individual card and
        // not all players of the race
        int raceOccurrenceInGame = getNumberOfTimesRaceHasBeenAddedToGame(race);
        String occurrenceSuffix = raceOccurrenceInGame == 0 ? "" : String.format("_%s", raceOccurrenceInGame);
        raceConverters.forEach(card -> card.setId(card.getId() + occurrenceSuffix));

        return raceConverters.stream().collect(Collectors.toMap(Card1::getId, card -> card));
    }

    private int getNumberOfTimesRaceHasBeenAddedToGame(RaceName1 race) {
        if (!numberOfRaceInstancesInGame.containsKey(race)) {
            numberOfRaceInstancesInGame.put(race, 0);
        }
        int raceOccurrenceInGame = numberOfRaceInstancesInGame.get(race);
        numberOfRaceInstancesInGame.replace(race, raceOccurrenceInGame + 1);
        return raceOccurrenceInGame;
    }

    private List<ConverterCard1> loadConverterCardsForRace(RaceName1 race) {
        List<ConverterCard1> converterCardList = DataUtils.loadListFromResource(converterCardsResource, new TypeReference<>() {
        });
        List<ConverterCard1> converterCardWithRace = converterCardList.stream().filter(card -> card.getRace().equals(race)).collect(Collectors.toList());
        log.info("Loaded {} converter cards for {}", converterCardWithRace.size(), race);
        return converterCardWithRace;
    }
}
