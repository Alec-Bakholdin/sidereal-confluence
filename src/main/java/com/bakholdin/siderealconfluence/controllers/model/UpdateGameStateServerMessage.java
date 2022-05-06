package com.bakholdin.siderealconfluence.controllers.model;

import com.bakholdin.siderealconfluence.model.Phase;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateGameStateServerMessage {
    private int turn;
    private Phase phase;
}
