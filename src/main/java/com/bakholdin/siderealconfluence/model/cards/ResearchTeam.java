package com.bakholdin.siderealconfluence.model.cards;

import com.bakholdin.siderealconfluence.model.Resources;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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

    public void flip() {
        if (isResearched) {
            return;
        }
        isResearched = true;
    }
}
