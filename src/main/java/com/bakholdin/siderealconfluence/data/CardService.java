package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.ConverterCard;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
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
public class CardService {
    @Getter
    private final Map<String, Card> cards = new HashMap<>();

    @Value(value = "classpath:cards/converterCards.json")
    private Resource converterCardsResource;

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper objectMapper = configureObjectMapper();

        List<ConverterCard> converterCards = objectMapper.readValue(converterCardsResource.getFile(), new TypeReference<>() {
        });
        Map<String, ConverterCard> converterCardMap = converterCards.stream().collect(Collectors.toMap(Card::getId, card -> card));
        cards.putAll(converterCardMap);

        log.info("Loaded {} cards", cards.size());
    }

    public Card getCard(String id) {
        return cards.get(id);
    }

    private ObjectMapper configureObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
