package com.bakholdin.siderealconfluence.repository;

import com.bakholdin.siderealconfluence.data.DataUtils;
import com.bakholdin.siderealconfluence.old_model.cards.Colony1;
import com.bakholdin.siderealconfluence.repository.entity.Card;
import com.bakholdin.siderealconfluence.repository.entity.CardType;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InitService {
    private final CardRepository cardRepository;

    @Value(value = "classpath:game_data/cards/colonies.json")
    private Resource coloniesResource;

    @PostConstruct
    private void init() {
        List<Colony1> colonies = DataUtils.loadListFromResource(coloniesResource, new TypeReference<>() {
        });
        List<Card> cardEntities = colonies.stream()
                .map(colony -> new Card(colony.getId(), colony.getName(), CardType.Colony))
                .collect(Collectors.toList());

        cardRepository.saveAll(cardEntities);
    }
}
