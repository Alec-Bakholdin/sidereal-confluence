package com.bakholdin.siderealconfluence.model.cards;

import com.bakholdin.siderealconfluence.model.Converter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Colony extends Card {
    private CardType type = CardType.Colony;
    private boolean isUpgraded = false;

    private Converter frontConverter;
    private Converter upgradeConverter;
    private Converter backConverter;

    private ColonyType frontType;
    private ColonyType backType;

    @Override
    public void flip() {
        isUpgraded = true;
    }
}
