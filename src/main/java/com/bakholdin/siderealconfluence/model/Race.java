package com.bakholdin.siderealconfluence.model;

import lombok.Data;

import java.util.List;

@Data
public class Race {
    private int colonySupport;
    private int tiebreaker;

    private int startingColonies;
    private int startingResearchTeams;
    private Resources startingResources;

    private List<String> startingConverterCards;
    private List<String> availableConverterCards;
}
