package com.bakholdin.siderealconfluence.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class GameState {
    private int turn = 1;
    private Phase phase = Phase.Trade;
    private Map<UUID, Player> players = new HashMap<>();
}
