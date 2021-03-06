package com.bakholdin.siderealconfluence.model.cards;

import com.bakholdin.siderealconfluence.model.Converter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Colony extends Card {
    private CardType type = CardType.Colony;
    private boolean isUpgraded = false;
    private boolean doubledWithCaylion = false;

    private Converter frontConverter;
    private Converter upgradeConverter;
    private Converter backConverter;

    private ColonyType frontType;
    private ColonyType backType;

    @Override
    public void flip() {
        isUpgraded = true;
    }

    @Override
    public List<Converter> activeConverters() {
        if (isUpgraded) {
            return List.of(upgradeConverter);
        }
        return List.of(frontConverter);
    }
}
