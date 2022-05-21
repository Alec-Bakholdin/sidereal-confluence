package com.bakholdin.siderealconfluence.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ResourcesDto {
    private int green;
    private int white;
    private int brown;
    private int black;
    private int blue;
    private int yellow;
    private int ships;
    private int octagon;
    private int points;
}
