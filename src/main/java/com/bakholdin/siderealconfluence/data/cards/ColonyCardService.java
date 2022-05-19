package com.bakholdin.siderealconfluence.data.cards;

import com.bakholdin.siderealconfluence.data.DataUtils;
import com.bakholdin.siderealconfluence.model.cards.Card1;
import com.bakholdin.siderealconfluence.model.cards.Colony1;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ColonyCardService {
    @Value(value = "classpath:game_data/cards/colonies.json")
    private Resource coloniesResource;
    private List<Colony1> availableColonies = new ArrayList<>();

    protected Map<String, Colony1> reset() {
        availableColonies = DataUtils.loadListFromResource(coloniesResource, new TypeReference<>() {
        });
        Map<String, Colony1> colonyMap = availableColonies.stream().collect(Collectors.toMap(Card1::getId, card -> card));
        Collections.shuffle(availableColonies);
        log.info("Loaded {} colonies", availableColonies.size());
        return colonyMap;
    }


    protected Colony1 draw() {
        if (availableColonies.isEmpty()) {
            throw new UnsupportedOperationException("No more colonies available");
        }
        return availableColonies.remove(0);
    }

    protected List<Colony1> draw(int n) {
        List<Colony1> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(draw());
        }
        return result;
    }
}
