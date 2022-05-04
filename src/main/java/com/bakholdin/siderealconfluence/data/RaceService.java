package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.Race;
import com.bakholdin.siderealconfluence.model.RaceEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
public class RaceService {
    private final Map<RaceEnum, Race> races = new HashMap<>();

    @Value(value = "classpath:races.json")
    private Resource raceJson;

    @PostConstruct
    private void initRaces() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<RaceEnum, Race> raceJsonRaces = objectMapper.readValue(raceJson.getFile(), new TypeReference<>() {
        });
        races.putAll(raceJsonRaces);
        log.info("Loaded {} races", races.size());
    }

    public Race get(RaceEnum race) {
        return races.get(race);
    }
}
