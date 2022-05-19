package com.bakholdin.siderealconfluence.service.model;

import com.bakholdin.siderealconfluence.model.BidTrackType1;
import com.bakholdin.siderealconfluence.model.Confluence1;
import com.bakholdin.siderealconfluence.model.Phase1;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder(builderClassName = "Builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateGameStateServerMessage {
    private Integer turn;
    private Phase1 phase;
    private Boolean gameOver;
    private Boolean gameStarted;

    private List<Confluence1> confluence1List;
    private List<String> availableColonies;
    private List<String> availableResearchTeams;
    private List<Integer> colonyBidTrack;
    private List<Integer> researchTeamBidTrack;

    private List<String> pendingResearches;

    @JsonInclude
    private UUID activeBidder;
    @JsonInclude
    private BidTrackType1 activeBidTrack;
}
