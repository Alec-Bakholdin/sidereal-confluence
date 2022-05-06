package com.bakholdin.siderealconfluence.model;

import lombok.Data;

import java.util.List;

@Data
public class ConfluenceBidTrack {
    private BidTrackType type;
    private int order;
    private List<Integer> shipMinima;
    private List<Integer> playerCounts;
}
