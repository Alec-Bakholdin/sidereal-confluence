package com.bakholdin.siderealconfluence.model.cards;

import com.bakholdin.siderealconfluence.model.Converter;
import lombok.Data;

import java.util.List;

@Data
public abstract class Card {
    private String id;
    private String name;
    private CardType type;

    public abstract void flip();

    public abstract List<Converter> activeConverters();
}
