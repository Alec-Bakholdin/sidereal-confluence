package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.BidTrackType;
import com.bakholdin.siderealconfluence.model.Confluence;
import com.bakholdin.siderealconfluence.model.ConfluenceBidTrack;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfluenceService {
    @Value(value = "classpath:game_data/confluenceCards.json")
    private Resource confluenceCardResource;
    @Value(value = "classpath:game_data/confluenceBidTracks.json")
    private Resource confluenceBidTrackResource;

    private List<Confluence> confluenceCards = null;
    private List<ConfluenceBidTrack> confluenceBidTracks = null;

    @PostConstruct
    private void init() {
        confluenceCards = DataUtils.loadListFromResource(confluenceCardResource, new TypeReference<>() {
        });
        confluenceBidTracks = DataUtils.loadListFromResource(confluenceBidTrackResource, new TypeReference<>() {
        });
    }

    public List<Confluence> getConfluenceCards(int numPlayers) {
        ValidationUtils.validatePlayerCount(numPlayers);
        return confluenceCards.stream()
                .filter(card -> card.getPlayerCounts().contains(numPlayers))
                .sorted(Comparator.comparingInt(Confluence::getTurn))
                .collect(Collectors.toList());
    }

    public List<Integer> getBidTrack(int numPlayers, BidTrackType type) {
        ValidationUtils.validatePlayerCount(numPlayers);
        return confluenceBidTracks.stream()
                .filter(track -> track.getType() == type && track.getPlayerCounts().contains(numPlayers))
                .sorted(Comparator.comparingInt(ConfluenceBidTrack::getOrder))
                .map(ConfluenceBidTrack::getShipMinima)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
