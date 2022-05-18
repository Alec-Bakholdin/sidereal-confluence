package com.bakholdin.siderealconfluence.model;

import com.bakholdin.siderealconfluence.model.cards.Card;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
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
    private List<String> researchedTechnologies;
    @JsonIgnore
    private PlayerBid playerBid;
    @JsonIgnore
    private List<Card> cards;
    @JsonIgnore
    private List<Card> inactiveCards;

    @JsonProperty("cards")
    public List<String> cardIds() {
        return cards != null ? cards.stream().map(Card::getId).collect(Collectors.toList()) : new ArrayList<>();
    }

    @JsonProperty("inactiveCards")
    public List<String> inactiveCardIds() {
        return inactiveCards != null ? inactiveCards.stream().map(Card::getId).collect(Collectors.toList()) : new ArrayList<>();
    }
}
