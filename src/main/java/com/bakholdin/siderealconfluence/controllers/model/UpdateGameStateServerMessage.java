package com.bakholdin.siderealconfluence.controllers.model;

import com.bakholdin.siderealconfluence.model.Confluence;
import com.bakholdin.siderealconfluence.model.Phase;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateGameStateServerMessage {
    private int turn;
    private Phase phase;
    private boolean isGameOver;
    private boolean isGameStarted;

    private List<Confluence> confluenceList;
    private List<String> availableColonies;
    private List<String> availableResearchTeams;
    private List<Integer> colonyBidTrack;
    private List<Integer> researchTeamBidTrack;
}
