package com.bakholdin.siderealconfluence.old_model;

import com.bakholdin.siderealconfluence.old_model.cards.Card1;
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
public class Player1 {
    private UUID id;
    private String name;
    private Resources1 resources;
    private Resources1 donations;
    private Race1 race;
    private boolean isReady;
    private List<String> researchedTechnologies;
    @JsonIgnore
    private PlayerBid1 playerBid;
    @JsonIgnore
    private List<Card1> card1s;
    @JsonIgnore
    private List<Card1> inactiveCard1s;

    @JsonProperty("cards")
    public List<String> cardIds() {
        return card1s != null ? card1s.stream().map(Card1::getId).collect(Collectors.toList()) : new ArrayList<>();
    }

    @JsonProperty("inactiveCards")
    public List<String> inactiveCardIds() {
        return inactiveCard1s != null ? inactiveCard1s.stream().map(Card1::getId).collect(Collectors.toList()) : new ArrayList<>();
    }
}
