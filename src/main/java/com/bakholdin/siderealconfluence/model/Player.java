package com.bakholdin.siderealconfluence.model;

import com.bakholdin.siderealconfluence.model.cards.Card;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class Player {
    private UUID id;
    private String name;
    private Resources resources;
    private Resources donations;
    private Race race;
    private boolean isReady;
    @JsonIgnore
    private List<Card> cards;
    @JsonIgnore
    private List<Card> inactiveCards;

    @JsonProperty("cards")
    public List<String> cardIds() {
        return cards.stream().map(Card::getId).collect(Collectors.toList());
    }

    @JsonProperty("inactiveCards")
    public List<String> inactiveCardIds() {
        return inactiveCards.stream().map(Card::getId).collect(Collectors.toList());
    }
}
