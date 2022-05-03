package com.bakholdin.siderealconfluence.model.cards;

import com.bakholdin.siderealconfluence.model.Converter;
import lombok.Data;

import java.util.List;

@Data
public class ConverterCard implements Card{
    private boolean isUpgraded = false;
    private List<Converter> upgradeOptions;

    private List<Converter> frontConverters;
    private List<Converter> backConverters;

    @Override
    public void flip() {
        isUpgraded = true;
    }
}
