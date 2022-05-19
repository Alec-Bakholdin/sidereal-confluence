package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.BidTrackType1;
import com.bakholdin.siderealconfluence.model.Confluence1;
import com.bakholdin.siderealconfluence.model.ConfluenceBidTrack1;
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

    private List<Confluence1> confluence1Cards = null;
    private List<ConfluenceBidTrack1> confluenceBidTrack1s = null;

    @PostConstruct
    private void init() {
        confluence1Cards = DataUtils.loadListFromResource(confluenceCardResource, new TypeReference<>() {
        });
        confluenceBidTrack1s = DataUtils.loadListFromResource(confluenceBidTrackResource, new TypeReference<>() {
        });
    }

    public List<Confluence1> getConfluenceCards(int numPlayers) {
        ValidationUtils.validatePlayerCount(numPlayers);
        return confluence1Cards.stream()
                .filter(card -> card.getPlayerCounts().contains(numPlayers))
                .sorted(Comparator.comparingInt(Confluence1::getTurn))
                .collect(Collectors.toList());
    }

    public List<Integer> getBidTrack(int numPlayers, BidTrackType1 type) {
        ValidationUtils.validatePlayerCount(numPlayers);
        return confluenceBidTrack1s.stream()
                .filter(track -> track.getType() == type && track.getPlayerCounts().contains(numPlayers))
                .sorted(Comparator.comparingInt(ConfluenceBidTrack1::getOrder))
                .map(ConfluenceBidTrack1::getShipMinima)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
