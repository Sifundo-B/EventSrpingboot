package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationMessageListener implements MessageListener {

    private final ObjectMapper objectMapper;

    public NotificationMessageListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody());
            // Assuming your notification is of type Notification
            Notification notification = objectMapper.readValue(messageBody, Notification.class);
            // Handle the notification
            System.out.println("Received notification: " + notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
