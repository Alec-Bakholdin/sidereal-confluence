package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.service.model.OutgoingSocketTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

@Log4j2
@ControllerAdvice
@RequiredArgsConstructor
public class SocketExceptionController extends DefaultHandlerExceptionResolver {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageExceptionHandler(UnsupportedOperationException.class)
    public void unsupportedOperation(UnsupportedOperationException e) {
        log.error(e);
        handleException(e, "Unsupported operation");
    }

    @MessageExceptionHandler(IllegalArgumentException.class)
    public void illegalArgument(IllegalArgumentException e) {
        log.error(e);
        handleException(e, "Illegal argument");
    }

    @MessageExceptionHandler(Exception.class)
    public void exception(Exception e) {
        log.error(e);
        handleException(e, null);
    }

    private void handleException(Exception e, String prefix) {
        String payload = prefix == null ? e.getMessage() : String.format("%s: %s", prefix, e.getMessage());
        if (payload == null) {
            payload = "Unknown error";
        }
        log.error("failed", e);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_ERROR, payload);
    }
}
