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
    private Phase phase = Phase.Trade;
    private boolean isGameStarted = false;
    private boolean isGameOver = false;

    private List<Confluence> confluenceList = new LinkedList<>();
    private List<String> availableColonies = new LinkedList<>();
    private List<String> availableResearchTeams = new LinkedList<>();
    private List<Integer> colonyBidTrack = new LinkedList<>();
    private List<Integer> researchTeamBidTrack = new LinkedList<>();

    private Map<UUID, Player> players = new HashMap<>();
}
