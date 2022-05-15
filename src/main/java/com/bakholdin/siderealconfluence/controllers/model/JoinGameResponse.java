package com.bakholdin.siderealconfluence.controllers.model;

import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.model.RaceName;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class JoinGameResponse {
    private UUID playerId;
    private String playerName;
    private GameState gameState;
    private RaceName raceName;
}
