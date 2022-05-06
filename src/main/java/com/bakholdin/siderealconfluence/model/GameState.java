package com.bakholdin.siderealconfluence.model;

import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class GameState {
    private int turn = 0;
    private boolean gameInSession = false;
    private boolean gameOver = false;
    private List<Confluence> confluenceList = new LinkedList<>();
    private Phase phase = Phase.Trade;
    private Map<UUID, Player> players = new HashMap<>();
}
