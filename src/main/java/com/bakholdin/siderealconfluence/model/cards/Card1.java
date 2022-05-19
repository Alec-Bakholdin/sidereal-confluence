package com.bakholdin.siderealconfluence.model.cards;

import com.bakholdin.siderealconfluence.model.Converter1;
import lombok.Data;

import java.util.List;

@Data
public abstract class Card1 {
    private String id;
    private String name;
    private CardType1 type;

    public abstract List<Converter1> activeConverters();
}
