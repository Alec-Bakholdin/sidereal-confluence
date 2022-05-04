package com.bakholdin.siderealconfluence.model.cards;

import com.bakholdin.siderealconfluence.model.Converter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConverterCard extends Card {
    private CardType type = CardType.ConverterCard;

    private boolean isUpgraded = false;
    private boolean isConsumed = false;
    private List<Converter> upgradeOptions;
    private List<Converter> acquisitionOptions;

    private List<Converter> frontConverters;
    private List<Converter> backConverters;

    @Override
    public void flip() {
        isUpgraded = true;
    }
}
