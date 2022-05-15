package com.bakholdin.siderealconfluence.controllers.model;

import lombok.Data;

@Data
public class SetPlayerBidsClientMessage {
    private String playerId;
    private int colonyBid;
    private int researchTeamBid;
}
