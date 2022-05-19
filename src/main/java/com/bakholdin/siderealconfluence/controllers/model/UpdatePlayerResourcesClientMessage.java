package com.bakholdin.siderealconfluence.controllers.model;

import com.bakholdin.siderealconfluence.model.Resources1;
import lombok.Data;

@Data
public class UpdatePlayerResourcesClientMessage {
    private String playerId;
    private Resources1 resources;
    private boolean donations;
}
