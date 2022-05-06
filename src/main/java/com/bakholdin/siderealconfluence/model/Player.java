package com.bakholdin.siderealconfluence.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Player {
    private UUID id;
    private String name;
    private Resources resources;
    private Race race;
    private boolean isReady;
    private List<String> cards;
}
