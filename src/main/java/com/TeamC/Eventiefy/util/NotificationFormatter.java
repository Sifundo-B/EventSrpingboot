package com.TeamC.Eventiefy.util;

import com.TeamC.Eventiefy.entity.Event;
import com.TeamC.Eventiefy.enums.NotificationType;

// A utility class for formatting notification messages.
public class NotificationFormatter {

    public static String formatNotificationMessage(NotificationType type, Event event, String organizerName, String rejectionComment) {
        StringBuilder messageBuilder = new StringBuilder();

        switch (type) {
            case EVENT_APPROVAL:
                messageBuilder.append("Dear ").append(organizerName).append(", your event '").append(event.getName()).append("' has been approved.");
                break;
            case EVENT_REJECTION:
                messageBuilder.append("Dear ").append(organizerName).append(", your event '").append(event.getName()).append("' has been rejected. Reason: \"").append(rejectionComment).append("\"");
                break;
            case EVENT_CREATION:
                messageBuilder.append("A new event titled '").append(event.getName()).append("' has been created.");
                break;
            default:
                messageBuilder.append("You have a new notification.");
                break;
        }

        return messageBuilder.toString();
    }
}