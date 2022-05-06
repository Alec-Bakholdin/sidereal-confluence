package com.bakholdin.siderealconfluence.controllers.model;

import com.bakholdin.siderealconfluence.model.Resources;
import lombok.Data;

@Data
public class UpdatePlayerResourcesClientMessage {
    private String playerId;
    private Resources resources;
}
