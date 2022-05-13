package com.bakholdin.siderealconfluence.service;

import com.bakholdin.siderealconfluence.controllers.model.UpdatePlayerResourcesServerMessage;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.service.model.OutgoingSocketTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;


    public void sendUpdatePlayerResourcesToClient(Player player) {
        UpdatePlayerResourcesServerMessage msg = UpdatePlayerResourcesServerMessage.builder()
                .resources(player.getResources())
                .playerId(player.getId().toString())
                .build();
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_PLAYER_UPDATED_RESOURCES, msg);
    }
}
