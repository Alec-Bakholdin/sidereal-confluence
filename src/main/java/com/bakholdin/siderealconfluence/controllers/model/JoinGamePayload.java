package com.bakholdin.siderealconfluence.controllers.model;

import com.bakholdin.siderealconfluence.model.RaceName1;
import lombok.Data;

@Data
public class JoinGamePayload {
    private String playerName;
    private RaceName1 raceName;
}
