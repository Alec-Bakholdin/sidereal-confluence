package com.bakholdin.siderealconfluence.model;

import com.bakholdin.siderealconfluence.model.cards.Colony;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam;
import lombok.Data;

import java.util.List;

@Data
public class Player {
    private String name;
    private Resources resources;
    private List<ResearchTeam> researchTeams;
    private List<Colony> colonies;
}
