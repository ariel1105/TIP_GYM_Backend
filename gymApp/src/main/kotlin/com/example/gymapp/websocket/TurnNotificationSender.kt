package com.example.gymapp.websocket

import com.example.gymapp.utils.TurnDTO
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class TurnNotificationSender(
    private val messagingTemplate: SimpMessagingTemplate
) {
    fun notifyTurnsScheduled(activityId: Long, turns: List<TurnDTO>) {
        messagingTemplate.convertAndSend("/topic/activity/$activityId", turns)
    }
}