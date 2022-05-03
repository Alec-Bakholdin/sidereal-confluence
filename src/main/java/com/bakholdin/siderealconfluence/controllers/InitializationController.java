package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.model.GameState;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class InitializationController {
    @GetMapping("/initializeGame")
    public GameState initializeGame() {
        return new GameState();
    }
}
