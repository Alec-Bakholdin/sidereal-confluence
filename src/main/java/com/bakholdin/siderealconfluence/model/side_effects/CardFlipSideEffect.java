package com.bakholdin.siderealconfluence.model.side_effects;

import com.bakholdin.siderealconfluence.model.cards.Card;
import lombok.Data;

@Data
public class CardFlipSideEffect implements SideEffect {
    private Card targetCard;

    @Override
    public boolean resolve() {
        targetCard.flip();
        return true;
    }
}
