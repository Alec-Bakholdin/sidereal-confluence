package com.bakholdin.siderealconfluence.service.model;

import com.bakholdin.siderealconfluence.model.Resources1;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePlayerResourcesServerMessage {
    private String playerId;
    private Resources1 resources;
    private Resources1 donations;
}
