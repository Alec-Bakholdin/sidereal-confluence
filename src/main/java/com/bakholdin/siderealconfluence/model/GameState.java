package com.bakholdin.siderealconfluence.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class GameState {
    private int turn = 0;
    private Phase phase = Phase.Trade;
    private boolean isGameStarted = false;
    private boolean isGameOver = false;

    private List<Confluence> confluenceList = new ArrayList<>();

    private List<String> availableColonies = new ArrayList<>();
    private List<String> availableResearchTeams = new ArrayList<>();

    private List<Integer> colonyBidTrack = new ArrayList<>();
    private List<Integer> researchTeamBidTrack = new ArrayList<>();

    private List<String> pendingResearches = new ArrayList<>();

    private UUID activeBidder;
    private BidTrackType activeBidTrack;
    private Map<UUID, Player> players = new HashMap<>();
}
