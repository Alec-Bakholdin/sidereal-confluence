package com.bakholdin.siderealconfluence.dto;

import com.bakholdin.siderealconfluence.enums.RaceType;
import lombok.Data;

@Data
public class RaceDto {
    private RaceType name;

    private Integer colonySupport;
    private Integer tiebreaker;
    private Integer startingColonies;
    private Integer startingResearchTeams;

    private ResourcesDto startingResources;
}
