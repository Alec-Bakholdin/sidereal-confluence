package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.Race;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

@Service
public class PlayerService {
    private final Map<UUID, Player> players = new HashMap<>();

    public Player createPlayer(String name, Race race) {
        Player newPlayer = Player.builder()
                .id(UUID.randomUUID())
                .name(name)
                .race(race)
                .resources(race.getStartingResources())
                .cards(new LinkedList<>())
                .build();

        players.put(newPlayer.getId(), newPlayer);
        return newPlayer;
    }

    public Player getPlayer(UUID id) {
        return players.get(id);
    }
}
