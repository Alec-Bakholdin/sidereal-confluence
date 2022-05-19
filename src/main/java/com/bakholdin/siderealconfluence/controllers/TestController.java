package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.data.DataUtils;
import com.bakholdin.siderealconfluence.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@RequiredArgsConstructor
public class TestController {
    private final CardRepository cardRepository;

    @MessageMapping("/test")
    @SendTo("/topic/greetings")
    public String greeting(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received message: " + message);
        String sessionId = DataUtils.getSessionHeader(headerAccessor, "sessionId");
        return "Hello, " + sessionId + "!";
    }

    @MessageMapping("/test2")
    @SendToUser("/queue/reply")
    public String processMessageFromClient(@Payload String message, SimpMessageHeaderAccessor headers) {
        String sessionId = DataUtils.getSessionHeader(headers, "sessionId");
        log.info("Received message from {}: {}", sessionId, message);
        return "Hello, " + message + "!";
    }
}
