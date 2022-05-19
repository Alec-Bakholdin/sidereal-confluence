package com.bakholdin.siderealconfluence.model;

import lombok.Data;

@Data
public class Race1 {
    private RaceName1 name;
    private int colonySupport;
    private int tiebreaker;

    private int startingColonies;
    private int startingResearchTeams;
    private Resources1 startingResources;
}
