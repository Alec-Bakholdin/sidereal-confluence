package com.bakholdin.siderealconfluence.model;

import lombok.Data;

import java.util.List;

@Data
public class ConfluenceBidTrack1 {
    private BidTrackType1 type;
    private int order;
    private List<Integer> shipMinima;
    private List<Integer> playerCounts;
}
