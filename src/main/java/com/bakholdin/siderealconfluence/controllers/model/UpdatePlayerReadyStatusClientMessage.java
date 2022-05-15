package com.bakholdin.siderealconfluence.controllers.model;

import lombok.Data;

@Data
public class UpdatePlayerReadyStatusClientMessage {
    private String playerId;
    private boolean isReady;
}
