package com.bakholdin.siderealconfluence.controllers.model;

import lombok.Data;

@Data
public class TransferCardClientMessage {
    private String currentOwnerPlayerId;
    private String newOwnerPlayerId;
    private String cardId;
}
