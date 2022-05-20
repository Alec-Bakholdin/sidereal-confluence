package com.bakholdin.siderealconfluence.old_service.model;

import com.bakholdin.siderealconfluence.old_model.Resources1;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePlayerResourcesServerMessage {
    private String playerId;
    private Resources1 resources;
    private Resources1 donations;
}
