package com.bakholdin.siderealconfluence.config;

import com.bakholdin.siderealconfluence.entity.Game;
import com.bakholdin.siderealconfluence.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
public class SubscribeEventInterceptor implements ChannelInterceptor {
    private static final String TOPIC_GAME_PREFIX = "/topic/game/";
    private final GameRepository gameRepository;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        String simpDestination = headerAccessor.getDestination();
        String username = Objects.requireNonNull(headerAccessor.getUser()).getName();

        Long gameId = tryParseGameId(simpDestination);
        if (gameId == null) {
            return message;
        }
        validateUserIsInGame(username, gameId);
        log.info("User {} subscribed to {}", username, simpDestination);
        return message;
    }

    private void validateUserIsInGame(String username, Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException(String.format("Game %d not found", gameId)));
        Set<String> presentUsernames = game.getPlayers().stream()
                .map(player -> player.getUser().getUsername())
                .collect(Collectors.toSet());
        if (!presentUsernames.contains(username)) {
            throw new RuntimeException(String.format("%s not present in game %d", username, gameId));
        }
    }

    private Long tryParseGameId(String simpDestination) {
        if (simpDestination == null || !simpDestination.startsWith(TOPIC_GAME_PREFIX)) {
            return null;
        }

        String numberFirst = simpDestination.substring(TOPIC_GAME_PREFIX.length());
        String gameIdStr = numberFirst.indexOf('/') > 0 ? numberFirst.substring(0, numberFirst.indexOf("/")) : "";
        try {
            return Long.parseLong(gameIdStr);
        } catch (NumberFormatException e) {
            log.warn("Invalid game id: " + gameIdStr);
        }
        return null;
    }
}
