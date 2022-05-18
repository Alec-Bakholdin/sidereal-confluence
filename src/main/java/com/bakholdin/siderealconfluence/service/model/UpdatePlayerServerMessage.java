package com.bakholdin.siderealconfluence.service.model;

import com.bakholdin.siderealconfluence.model.Race;
import com.bakholdin.siderealconfluence.model.Resources;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdatePlayerServerMessage {
    @NonNull
    private UUID playerId;
    private String name;
    private Resources resources;
    private Resources donations;
    private Race race;
    private Boolean isReady;
    private List<String> researchedTechnologies;
    private List<String> cards;
    private List<String> inactiveCards;
}
