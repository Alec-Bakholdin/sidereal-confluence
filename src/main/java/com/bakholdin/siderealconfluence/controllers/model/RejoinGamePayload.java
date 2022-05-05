package com.bakholdin.siderealconfluence.controllers.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RejoinGamePayload {
    private String playerId;
    private String playerName;
}
