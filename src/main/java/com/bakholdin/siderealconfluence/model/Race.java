package com.bakholdin.siderealconfluence.model;

import com.bakholdin.siderealconfluence.model.Resources;
import lombok.Data;

@Data
public class Race {
    private int colonySupport;
    private int tiebreaker;

    private int startingColonies;
    private int startingResearchTeams;
    private Resources startingResources;
}
