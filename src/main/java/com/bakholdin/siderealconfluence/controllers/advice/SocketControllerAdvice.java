package com.bakholdin.siderealconfluence.controllers.advice;

import com.bakholdin.siderealconfluence.controllers.socket.OutgoingSocketTopics;
import com.bakholdin.siderealconfluence.dto.ErrorDto;
import com.bakholdin.siderealconfluence.entity.User;
import com.bakholdin.siderealconfluence.exceptions.GameException;
import com.bakholdin.siderealconfluence.exceptions.UserException;
import com.bakholdin.siderealconfluence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.security.Principal;

@Log4j2
@ControllerAdvice
@RequiredArgsConstructor
public class SocketControllerAdvice extends DefaultHandlerExceptionResolver {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRepository userRepository;

    @MessageExceptionHandler(RuntimeException.class)
    public void handleRuntimeException(RuntimeException e, @AuthenticationPrincipal Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        ErrorDto errorDto = buildErrorDtoAndLogError(e);
        if (user.getGame() != null) {
            String errorTopic = OutgoingSocketTopics.TOPIC_GAME_ERRORS(user.getGame().getId());
            simpMessagingTemplate.convertAndSend(errorTopic, errorDto);
        } else {
            simpMessagingTemplate.convertAndSendToUser(principal.getName(), OutgoingSocketTopics.USER_ERRORS, errorDto);
        }
    }

    @MessageExceptionHandler(UserException.class)
    public void handleUserException(UserException e, @AuthenticationPrincipal Principal principal) {
        ErrorDto errorDto = buildErrorDtoAndLogError(e);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), OutgoingSocketTopics.USER_ERRORS, errorDto);
    }

    @MessageExceptionHandler(GameException.class)
    public void handleGameException(GameException e) {
        ErrorDto errorDto = buildErrorDtoAndLogError(e);
        String gameErrorTopic = OutgoingSocketTopics.TOPIC_GAME_ERRORS(e.getGameId());
        simpMessagingTemplate.convertAndSend(gameErrorTopic, errorDto);
    }

    private ErrorDto buildErrorDtoAndLogError(Exception e) {
        log.error(e.getMessage(), e);
        return ErrorDto.builder()
                .message(e.getMessage())
                .build();
    }
}
