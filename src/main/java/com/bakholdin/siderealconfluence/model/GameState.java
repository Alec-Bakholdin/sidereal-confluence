package com.bakholdin.siderealconfluence.model;

import com.bakholdin.siderealconfluence.model.cards.Colony;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam;
import lombok.Data;

import java.util.List;

@Data
public class GameState {
    private int turn;
    private Phase currentPhase;
    private List<Confluence> confluenceCards;

    private List<Player> players;
    private List<ResearchTeam> availableResearchTeams;
    private List<Colony> availableColonies;
}
