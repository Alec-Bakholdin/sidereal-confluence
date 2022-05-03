package com.bakholdin.siderealconfluence.model.cards;

import com.bakholdin.siderealconfluence.model.Converter;
import lombok.Data;

import java.util.List;

@Data
public class ResearchTeam implements Card{
    private String name;
    private String resultingTechnology;
    private List<Converter> converters;
    private boolean isResearched = false;

    public void flip() {
        if(isResearched) {
            return;
        }
        isResearched = true;
    }
}
