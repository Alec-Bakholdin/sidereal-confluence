package com.bakholdin.siderealconfluence.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Principal {
    private String username;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GameDto game;

    @Override
    @JsonIgnore
    public String getName() {
        return username;
    }
}
