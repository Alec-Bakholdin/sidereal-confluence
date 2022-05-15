package com.bakholdin.siderealconfluence.service.model;

import com.bakholdin.siderealconfluence.model.Confluence;
import com.bakholdin.siderealconfluence.model.Phase;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(builderClassName = "Builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateGameStateServerMessage {
    private Integer turn;
    private Phase phase;
    private Boolean gameOver;
    private Boolean gameStarted;

    private List<Confluence> confluenceList;
    private List<String> availableColonies;
    private List<String> availableResearchTeams;
    private List<Integer> colonyBidTrack;
    private List<Integer> researchTeamBidTrack;

    private List<String> pendingResearches;
}
