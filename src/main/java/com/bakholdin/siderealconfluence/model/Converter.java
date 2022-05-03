package com.bakholdin.siderealconfluence.model;


import com.bakholdin.siderealconfluence.model.side_effects.SideEffect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Converter {
    private Resources input = new Resources();
    private Resources output = new Resources();
    @JsonIgnore
    private SideEffect sideEffect;

    private Phase phase;

    public void resolve(Player player) {

    }
}
