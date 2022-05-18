package com.bakholdin.siderealconfluence.model.cards;

import com.bakholdin.siderealconfluence.model.Converter;
import com.bakholdin.siderealconfluence.model.RaceName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConverterCard extends Card {
    private CardType type = CardType.ConverterCard;

    private int era;
    private boolean isStarting;
    private RaceName race;

    private boolean isUpgraded = false;
    private boolean isConsumed = false;
    private String upgradeTech1;
    private String upgradeTech2;
    private String upgradedName;
    //private List<Converter> acquisitionOptions;

    private List<Converter> frontConverters;
    private List<Converter> backConverters;

    @Override
    public List<Converter> activeConverters() {
        if (isUpgraded) {
            return backConverters;
        }
        return frontConverters;
    }
}
