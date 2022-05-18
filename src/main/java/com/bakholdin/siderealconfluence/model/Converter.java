package com.bakholdin.siderealconfluence.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Converter {
    private Resources input = new Resources();
    private Resources output = new Resources();
    private Resources donations = new Resources();

    private Phase phase;
}
