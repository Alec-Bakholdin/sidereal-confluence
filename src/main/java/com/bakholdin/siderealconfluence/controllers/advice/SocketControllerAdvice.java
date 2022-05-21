package com.bakholdin.siderealconfluence.controllers.advice;

import com.bakholdin.siderealconfluence.controllers.socket.OutgoingSocketTopics;
import com.bakholdin.siderealconfluence.dto.ErrorDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@RequiredArgsConstructor
public class SocketControllerAdvice {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageExceptionHandler(RuntimeException.class)
    public void handleException(RuntimeException e) {
        //UserDto userDto = SocketUtil.getUserDto(principal);
        UserDto userDto = UserDto.builder().build();
        ErrorDto errorDto = ErrorDto.builder()
                .message(e.getMessage())
                .build();
        if (userDto.getGame() != null) {
            String errorTopic = OutgoingSocketTopics.TOPIC_GAME_ERRORS(userDto.getGame().getId());
            simpMessagingTemplate.convertAndSend(errorTopic, errorDto);
        } else {
            simpMessagingTemplate.convertAndSendToUser(userDto.getName(), OutgoingSocketTopics.USER_ERRORS, errorDto);
        }
    }
}
