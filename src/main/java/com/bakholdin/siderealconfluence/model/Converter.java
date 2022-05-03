package com.bakholdin.siderealconfluence.model;


import com.bakholdin.siderealconfluence.model.side_effects.SideEffect;
import lombok.Data;

@Data
public class Converter {
    private Resources input;
    private Resources output;
    private SideEffect sideEffect;

    private Phase phase;

    public void resolve(Player player) {

    }
}
