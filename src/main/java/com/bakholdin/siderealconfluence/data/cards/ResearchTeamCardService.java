package com.bakholdin.siderealconfluence.data.cards;

import com.bakholdin.siderealconfluence.data.DataUtils;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ResearchTeamCardService {
    @Value(value = "classpath:game_data/cards/researchTeams.json")
    private Resource researchTeamsResource;

    private List<ResearchTeam> availableResearchTeams = new ArrayList<>();

    protected ResearchTeam draw() {
        if (availableResearchTeams.isEmpty()) {
            throw new RuntimeException("No more research teams available");
        }
        return availableResearchTeams.remove(0);
    }

    protected List<ResearchTeam> draw(int n) {
        List<ResearchTeam> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(draw());
        }
        return result;
    }


    protected Map<String, ResearchTeam> reset() {
        availableResearchTeams = DataUtils.loadListFromResource(researchTeamsResource, new TypeReference<>() {
        });
        Map<String, ResearchTeam> researchTeamMap = availableResearchTeams.stream().collect(Collectors.toMap(Card::getId, card -> card));

        // order randomly within each era
        Collections.shuffle(availableResearchTeams);
        availableResearchTeams.sort(Comparator.comparingInt(ResearchTeam::getEra));
        log.info("Loaded {} research teams", availableResearchTeams.size());
        return researchTeamMap;
    }
}
