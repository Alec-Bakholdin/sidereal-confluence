package com.bakholdin.siderealconfluence.dto;

import lombok.Data;

@Data
public class ResearchTeamDto {
    private String name;

    private Integer era;
    private Integer points;
    private String resultingTechnology;
    
    private ResourcesDto researchOptions;
}
