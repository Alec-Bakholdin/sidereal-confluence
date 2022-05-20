package com.bakholdin.siderealconfluence.old_model.cards;

import com.bakholdin.siderealconfluence.old_model.Converter1;
import com.bakholdin.siderealconfluence.old_model.Resources1;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ResearchTeam1 extends Card1 {
    private CardType1 type = CardType1.ResearchTeam;

    private int era;
    private String resultingTechnology;
    private Resources1 researchOptions;
    private int points;
    private boolean isResearched = false;

    @Override
    public List<Converter1> activeConverters() {
        throw new UnsupportedOperationException("Research teams do not have active converters");
    }
}
