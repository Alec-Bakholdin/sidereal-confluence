package com.bakholdin.siderealconfluence.controllers.model;

import com.bakholdin.siderealconfluence.model.Resources;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePlayerResourcesServerMessage {
    private String playerId;
    private Resources resources;
}
