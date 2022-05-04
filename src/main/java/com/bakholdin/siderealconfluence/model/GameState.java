package com.bakholdin.siderealconfluence.model;

import com.bakholdin.siderealconfluence.model.cards.Card;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class GameState {
    private int turn = 1;
    private Map<UUID, Player> players = new HashMap<>();
    private Map<String, Card> availableCards = new HashMap<>();
}
