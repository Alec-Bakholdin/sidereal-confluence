package com.bakholdin.siderealconfluence.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PlayerBid {
    private UUID playerId;
    private double colonyBid;
    private double researchTeamBid;
}
