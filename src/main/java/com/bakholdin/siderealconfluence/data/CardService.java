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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

    private Map<String, Card> allCards = null;
    private List<Colony> availableColonies = null;
    private List<Colony> takenColonies = null;
    private List<ResearchTeam> availableResearchTeams = null;
    private List<ResearchTeam> takenResearchTeams = null;


    public Map<String, Card> resetCards() {
        ObjectMapper objectMapper = configureObjectMapper();
        allCards = new HashMap<>();
        takenColonies = new ArrayList<>();
        takenResearchTeams = new ArrayList<>();
        try {
            List<ConverterCard> converterCardList = objectMapper.readValue(converterCardsResource.getFile(), new TypeReference<>() {
            });
            Map<String, ConverterCard> converterCardMap = converterCardList.stream().collect(Collectors.toMap(Card::getId, card -> card));
            allCards.putAll(converterCardMap);

            availableResearchTeams = objectMapper.readValue(researchTeamsResource.getFile(), new TypeReference<>() {
            });
            Map<String, ResearchTeam> researchTeamMap = availableResearchTeams.stream().collect(Collectors.toMap(Card::getId, card -> card));
            allCards.putAll(researchTeamMap);

            availableColonies = objectMapper.readValue(coloniesResource.getFile(), new TypeReference<>() {
            });
            Map<String, Colony> colonyMap = availableColonies.stream().collect(Collectors.toMap(Card::getId, card -> card));
            allCards.putAll(colonyMap);
            log.info("Loaded {} converter cards, {} research teams, and {} colonies", converterCardMap.size(), researchTeamMap.size(), colonyMap.size());
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
        int index = new Random().nextInt(availableColonies.size());
        Colony colony = availableColonies.remove(index);
        takenColonies.add(colony);
        return colony;
    }

    public ResearchTeam extractRandomResearchTeam() {
        if (availableResearchTeams.isEmpty()) {
            throw new RuntimeException("No more research teams available");
        }
        int index = new Random().nextInt(availableResearchTeams.size());
        ResearchTeam researchTeam = availableResearchTeams.remove(index);
        takenResearchTeams.add(researchTeam);
        return researchTeam;
    }

    private ObjectMapper configureObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

}
