package com.bakholdin.siderealconfluence.model.cards;

import com.bakholdin.siderealconfluence.model.Converter1;
import com.bakholdin.siderealconfluence.model.RaceName1;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConverterCard1 extends Card1 {
    private CardType1 type = CardType1.ConverterCard;

    private int era;
    private boolean isStarting;
    private RaceName1 race;

    private boolean isUpgraded = false;
    private boolean isConsumed = false;
    private String upgradeTech1;
    private String upgradeTech2;
    private String upgradedName;
    //private List<Converter> acquisitionOptions;

    private List<Converter1> frontConverter1s;
    private List<Converter1> backConverter1s;

    @Override
    public List<Converter1> activeConverters() {
        if (isUpgraded) {
            return backConverter1s;
        }
        return frontConverter1s;
    }
}
