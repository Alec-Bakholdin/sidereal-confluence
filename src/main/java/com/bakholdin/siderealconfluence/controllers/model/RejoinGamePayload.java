package com.bakholdin.siderealconfluence.controllers.model;

import com.bakholdin.siderealconfluence.model.RaceName1;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RejoinGamePayload {
    private String playerId;
    private String playerName;
    private RaceName1 raceName;
}
