package com.bakholdin.siderealconfluence.model;

import lombok.Data;

@Data
public class Race {
    private RaceName name;
    private int colonySupport;
    private int tiebreaker;

    private int startingColonies;
    private int startingResearchTeams;
    private Resources startingResources;
}
