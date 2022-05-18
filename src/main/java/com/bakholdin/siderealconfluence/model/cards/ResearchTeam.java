package com.bakholdin.siderealconfluence.model.cards;

import com.bakholdin.siderealconfluence.model.Converter;
import com.bakholdin.siderealconfluence.model.Resources;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ResearchTeam extends Card {
    private CardType type = CardType.ResearchTeam;

    private int era;
    private String resultingTechnology;
    private Resources researchOptions;
    private int points;
    private boolean isResearched = false;

    @Override
    public List<Converter> activeConverters() {
        throw new UnsupportedOperationException("Research teams do not have active converters");
    }
}
