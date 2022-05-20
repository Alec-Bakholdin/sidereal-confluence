package com.bakholdin.siderealconfluence.old_model.cards;

import com.bakholdin.siderealconfluence.old_model.Converter1;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Colony1 extends Card1 {
    private CardType1 type = CardType1.Colony;
    private boolean isUpgraded = false;
    private boolean doubledWithCaylion = false;

    private Converter1 frontConverter1;
    private Converter1 upgradeConverter1;
    private Converter1 backConverter1;

    private ColonyType1 frontType;
    private ColonyType1 backType;

    @Override
    public List<Converter1> activeConverters() {
        if (isUpgraded) {
            return List.of(upgradeConverter1);
        }
        return List.of(frontConverter1);
    }
}
