package com.bakholdin.siderealconfluence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PlayerGameState extends GameState {
    private Player self;
}
