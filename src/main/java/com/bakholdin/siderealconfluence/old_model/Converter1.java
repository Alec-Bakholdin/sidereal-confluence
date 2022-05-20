package com.bakholdin.siderealconfluence.old_model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Converter1 {
    private Resources1 input = new Resources1();
    private Resources1 output = new Resources1();
    private Resources1 donations = new Resources1();

    private Phase1 phase;
}
