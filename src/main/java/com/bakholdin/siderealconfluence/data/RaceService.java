package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.model.Race1;
import com.bakholdin.siderealconfluence.model.RaceName1;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class RaceService {
    private final CardService cardService;

    private final Map<RaceName1, Race1> races = new HashMap<>();

    @Value(value = "classpath:game_data/races.json")
    private Resource raceJson;

    @PostConstruct
    private void initRaces() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Race1> jsonRaceList = objectMapper.readValue(raceJson.getFile(), new TypeReference<>() {
        });
        Map<RaceName1, Race1> raceMap = jsonRaceList.stream().collect(Collectors.toMap(Race1::getName, race -> race));
        races.putAll(raceMap);
        log.info("Loaded {} races", races.size());
    }

    public Race1 initializeRaceForGame(RaceName1 raceName) {
        return races.get(raceName);
    }
}
