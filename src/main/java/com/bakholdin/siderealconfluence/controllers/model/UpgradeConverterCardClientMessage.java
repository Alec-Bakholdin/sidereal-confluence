package com.bakholdin.siderealconfluence.controllers.model;

import lombok.Data;

@Data
public class UpgradeConverterCardClientMessage {
    private String playerId;
    private String cardId;
    private String technology;
}
