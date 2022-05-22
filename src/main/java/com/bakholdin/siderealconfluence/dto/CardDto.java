package com.bakholdin.siderealconfluence.dto;

import com.bakholdin.siderealconfluence.enums.CardType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDto {
    private String id;
    private CardType cardType;

    private ConverterCardDto converterCard;
    private ColonyDto colony;
    private ResearchTeamDto researchTeam;
}
