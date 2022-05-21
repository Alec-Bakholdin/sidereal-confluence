package com.bakholdin.siderealconfluence.init;

import com.bakholdin.siderealconfluence.entity.Race;
import com.bakholdin.siderealconfluence.enums.RaceType;
import com.bakholdin.siderealconfluence.repository.RaceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RaceInit {

    private final RaceRepository raceRepository;
    @Value("classpath:game_data/races.json")
    private Resource racesResource;

    @PostConstruct
    public void init() {
        Set<RaceType> presentRaces = raceRepository.findAll().stream()
                .map(Race::getName)
                .collect(Collectors.toSet());
        List<Race> raceList = InitUtils.readListFromResource(racesResource, new TypeReference<>() {
        });
        raceRepository.saveAll(raceList.stream()
                .filter(race -> !presentRaces.contains(race.getName()))
                .collect(Collectors.toList())
        );
    }
}
