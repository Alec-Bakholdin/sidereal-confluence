package com.bakholdin.siderealconfluence.model.cards;

import com.bakholdin.siderealconfluence.model.Converter;
import lombok.Data;

@Data
public class Colony implements Card {
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
