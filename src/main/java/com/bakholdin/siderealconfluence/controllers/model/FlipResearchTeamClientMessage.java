package com.bakholdin.siderealconfluence.controllers.model;

import com.bakholdin.siderealconfluence.model.Resources;
import lombok.Data;

@Data
public class FlipResearchTeamClientMessage {
    private String cardId;
    private String playerId;
    private Resources cost;
}
