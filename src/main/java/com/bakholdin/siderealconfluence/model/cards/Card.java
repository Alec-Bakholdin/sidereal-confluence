package com.bakholdin.siderealconfluence.model.cards;

import lombok.Data;

@Data
public abstract class Card {
    private String id;
    private String name;
    private CardType type;

    public abstract void flip();
}
