package com.bakholdin.siderealconfluence.controllers.advice;

import com.bakholdin.siderealconfluence.controllers.socket.OutgoingSocketTopics;
import com.bakholdin.siderealconfluence.dto.ErrorDto;
import com.bakholdin.siderealconfluence.entity.User;
import com.bakholdin.siderealconfluence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class SocketControllerAdvice extends DefaultHandlerExceptionResolver {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRepository userRepository;

    @MessageExceptionHandler(RuntimeException.class)
    public void handleException(RuntimeException e, @AuthenticationPrincipal Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        ErrorDto errorDto = ErrorDto.builder()
                .message(e.getMessage())
                .build();
        if (user.getGame() != null) {
            String errorTopic = OutgoingSocketTopics.TOPIC_GAME_ERRORS(user.getGame().getId());
            simpMessagingTemplate.convertAndSend(errorTopic, errorDto);
        } else {
            simpMessagingTemplate.convertAndSendToUser(principal.getName(), OutgoingSocketTopics.USER_ERRORS, errorDto);
        }
    }
}
