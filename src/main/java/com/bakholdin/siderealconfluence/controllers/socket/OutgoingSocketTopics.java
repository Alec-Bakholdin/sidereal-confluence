package com.bakholdin.siderealconfluence.controllers.socket;

public class OutgoingSocketTopics {
    public static final String USER_ERRORS = "/queue/errors";

    public static String TOPIC_GAME_UPDATE_GAME(Long gameId) {
        return String.format("/topic/game/%d/updateGame", gameId);
    }

    public static String TOPIC_GAME_UPDATE_PLAYER(Long gameId) {
        return String.format("/topic/game/%d/updatePlayer", gameId);
    }

    public static String TOPIC_GAME_PLAYER_JOINED(Long gameId) {
        return String.format("/topic/game/%d/playerJoined", gameId);
    }

    public static String TOPIC_GAME_ERRORS(Long gameId) {
        return String.format("/topic/game/%d/errors", gameId);
    }
}
