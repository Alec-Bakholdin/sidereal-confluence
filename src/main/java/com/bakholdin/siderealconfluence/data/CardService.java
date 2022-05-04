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
import java.util.HashMap;
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

    private Map<String, Card> allCards = null;
    private Map<String, Colony> colonies = null;
    private Map<String, ConverterCard> converterCards = null;
    private Map<String, ResearchTeam> researchTeams = null;


    public Map<String, Card> resetCards() {
        ObjectMapper objectMapper = configureObjectMapper();
        allCards = new HashMap<>();
        try {
            List<ConverterCard> converterCardList = objectMapper.readValue(converterCardsResource.getFile(), new TypeReference<>() {
            });
            converterCards = converterCardList.stream().collect(Collectors.toMap(Card::getId, card -> card));
            allCards.putAll(converterCards);

            List<ResearchTeam> researchTeamList = objectMapper.readValue(researchTeamsResource.getFile(), new TypeReference<>() {
            });
            researchTeams = researchTeamList.stream().collect(Collectors.toMap(Card::getId, card -> card));
            allCards.putAll(researchTeams);

            List<Colony> colonyList = objectMapper.readValue(coloniesResource.getFile(), new TypeReference<>() {
            });
            colonies = colonyList.stream().collect(Collectors.toMap(Card::getId, card -> card));
            allCards.putAll(colonies);
            log.info("Loaded {} converter cards, {} research teams, and {} colonies", converterCards.size(), researchTeams.size(), colonies.size());
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

    private ObjectMapper configureObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

}
