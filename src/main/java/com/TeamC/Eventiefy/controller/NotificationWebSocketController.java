package com.TeamC.Eventiefy.controller;

import com.TeamC.Eventiefy.entity.Notification;
import com.TeamC.Eventiefy.services.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationWebSocketController {

    @Autowired
    NotificationServiceImpl notificationService;

    //controller to handle incoming WebSocket messages
    @MessageMapping("/notifications")
    @SendTo("/topic/notifications")
    public Notification sendNotification(Notification notification) {
        return notificationService.createNotification(notification);
    }
}