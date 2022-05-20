package com.bakholdin.siderealconfluence.old_model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PlayerBid1 {
    private UUID playerId;
    private double colonyBid;
    private double researchTeamBid;
}
