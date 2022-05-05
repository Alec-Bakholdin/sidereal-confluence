package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.Colony;
import com.bakholdin.siderealconfluence.model.cards.ConverterCard;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CardService {

    @Value(value = "classpath:cards/converterCards.json")
    private Resource converterCardsResource;
    @Value(value = "classpath:cards/researchTeams.json")
    private Resource researchTeamsResource;
    @Value(value = "classpath:cards/colonies.json")
    private Resource coloniesResource;

    private Map<String, Card> allCards = new HashMap<>();
    private List<Colony> availableColonies = new LinkedList<>();
    private List<ResearchTeam> availableResearchTeams = new LinkedList<>();


    public Map<String, Card> resetCards() {
        allCards = new HashMap<>();
        try {
            loadConverterCards();
            loadResearchTeams();
            loadColonies();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return allCards;
    }

    public Map<String, Card> getCurrentGameCards() {
        if (allCards == null) {
            return resetCards();
        }
        return allCards;
    }

    public Card get(String id) {
        return getCurrentGameCards().get(id);
    }

    public Colony extractRandomColony() {
        if (availableColonies.isEmpty()) {
            throw new RuntimeException("No more colonies available");
        }
        return availableColonies.remove(0);
    }

    public ResearchTeam extractRandomResearchTeam() {
        if (availableResearchTeams.isEmpty()) {
            throw new RuntimeException("No more research teams available");
        }
        return availableResearchTeams.remove(0);
    }


    private void loadColonies() throws IOException {
        ObjectMapper objectMapper = configureObjectMapper();
        availableColonies = objectMapper.readValue(coloniesResource.getFile(), new TypeReference<>() {
        });
        Map<String, Colony> colonyMap = availableColonies.stream().collect(Collectors.toMap(Card::getId, card -> card));
        allCards.putAll(colonyMap);
        Collections.shuffle(availableColonies);
        log.info("Loaded {} colonies", availableColonies.size());
    }

    private void loadResearchTeams() throws IOException {
        ObjectMapper objectMapper = configureObjectMapper();
        availableResearchTeams = objectMapper.readValue(researchTeamsResource.getFile(), new TypeReference<>() {
        });
        Map<String, ResearchTeam> researchTeamMap = availableResearchTeams.stream().collect(Collectors.toMap(Card::getId, card -> card));
        allCards.putAll(researchTeamMap);

        // order randomly within each era
        Collections.shuffle(availableResearchTeams);
        availableResearchTeams.sort(Comparator.comparingInt(ResearchTeam::getEra));
        log.info("Loaded {} research teams", availableResearchTeams.size());
    }

    private void loadConverterCards() throws IOException {
        ObjectMapper objectMapper = configureObjectMapper();
        List<ConverterCard> converterCardList = objectMapper.readValue(converterCardsResource.getFile(), new TypeReference<>() {
        });
        Map<String, ConverterCard> converterCardMap = converterCardList.stream().collect(Collectors.toMap(Card::getId, card -> card));
        allCards.putAll(converterCardMap);
        log.info("Loaded {} converter cards", converterCardList.size());
    }

    private ObjectMapper configureObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

}
