package com.bakholdin.siderealconfluence.exceptions;

import lombok.Getter;

@Getter
public class GameException extends RuntimeException {
    private final Long gameId;

    public GameException(Long gameId, String message) {
        super(message);
        this.gameId = gameId;
    }
}
