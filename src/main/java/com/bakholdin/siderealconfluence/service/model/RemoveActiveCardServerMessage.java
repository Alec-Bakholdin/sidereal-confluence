package com.bakholdin.siderealconfluence.service.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RemoveActiveCardServerMessage {
    private UUID playerId;
    private String cardId;
}
