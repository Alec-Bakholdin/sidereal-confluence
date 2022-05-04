package com.bakholdin.siderealconfluence.model.cards;

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

    private String resultingTechnology;
    private List<Resources> researchOptions;
    private int points;
    private boolean isResearched = false;

    public void flip() {
        if (isResearched) {
            return;
        }
        isResearched = true;
    }
}
