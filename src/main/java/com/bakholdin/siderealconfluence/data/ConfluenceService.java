package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.Confluence;
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
    private List<Confluence> confluenceCards = null;

    @PostConstruct
    private void init() {
        confluenceCards = ResourceUtils.loadListFromResource(confluenceCardResource);
    }

    public List<Confluence> getConfluenceCards(int numPlayers) {
        if (numPlayers < 4 || numPlayers > 10) {
            throw new IllegalArgumentException("Invalid number of players: " + numPlayers);
        }
        return confluenceCards.stream()
                .filter(card -> card.getPlayerCounts().contains(numPlayers))
                .sorted(Comparator.comparingInt(Confluence::getTurn))
                .collect(Collectors.toList());
    }
}
