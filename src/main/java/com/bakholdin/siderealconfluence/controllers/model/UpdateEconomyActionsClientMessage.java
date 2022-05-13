package com.bakholdin.siderealconfluence.controllers.model;

import lombok.Data;

import java.util.List;

@Data
public class UpdateEconomyActionsClientMessage {
    private String playerId;
    private List<EconomyAction> actions;
}
