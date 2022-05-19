package com.bakholdin.siderealconfluence.data.cards;

import com.bakholdin.siderealconfluence.data.DataUtils;
import com.bakholdin.siderealconfluence.model.cards.Card1;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam1;
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

    private List<ResearchTeam1> availableResearchTeam1s = new ArrayList<>();

    protected ResearchTeam1 draw() {
        if (availableResearchTeam1s.isEmpty()) {
            throw new RuntimeException("No more research teams available");
        }
        return availableResearchTeam1s.remove(0);
    }

    protected List<ResearchTeam1> draw(int n) {
        List<ResearchTeam1> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(draw());
        }
        return result;
    }


    protected Map<String, ResearchTeam1> reset() {
        availableResearchTeam1s = DataUtils.loadListFromResource(researchTeamsResource, new TypeReference<>() {
        });
        Map<String, ResearchTeam1> researchTeamMap = availableResearchTeam1s.stream().collect(Collectors.toMap(Card1::getId, card -> card));

        // order randomly within each era
        Collections.shuffle(availableResearchTeam1s);
        availableResearchTeam1s.sort(Comparator.comparingInt(ResearchTeam1::getEra));
        log.info("Loaded {} research teams", availableResearchTeam1s.size());
        return researchTeamMap;
    }
}
