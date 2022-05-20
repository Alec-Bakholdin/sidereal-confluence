package com.bakholdin.siderealconfluence.old_service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferCardServerMessage {
    private String currentOwnerPlayerId;
    private String newOwnerPlayerId;
    private String cardId;
}
