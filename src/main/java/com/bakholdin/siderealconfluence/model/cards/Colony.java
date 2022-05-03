package com.bakholdin.siderealconfluence.model.cards;

import com.bakholdin.siderealconfluence.model.Converter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Colony extends Card {
    private String name;
    private boolean isUpgraded;

    private Converter frontConverter;
    private Converter upgradeConverter;
    private Converter backConverter;


    @Override
    public void flip() {
        isUpgraded = true;
    }
}
