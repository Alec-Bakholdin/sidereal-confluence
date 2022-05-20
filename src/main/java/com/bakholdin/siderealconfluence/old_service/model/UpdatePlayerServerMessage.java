package com.bakholdin.siderealconfluence.old_service.model;

import com.bakholdin.siderealconfluence.old_model.Race1;
import com.bakholdin.siderealconfluence.old_model.Resources1;
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
    private Resources1 resources;
    private Resources1 donations;
    private Race1 race;
    private Boolean isReady;
    private List<String> researchedTechnologies;
    private List<String> cards;
    private List<String> inactiveCards;
}
