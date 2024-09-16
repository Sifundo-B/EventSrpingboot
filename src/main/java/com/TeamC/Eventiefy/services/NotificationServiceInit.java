package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.Event;
import com.TeamC.Eventiefy.entity.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationServiceInit {
    Notification createNotification(Notification notification);

    List<Notification> getAllNotifications();

    List<Notification> getNotificationsForUser(Long userId);

    Optional<Notification> getNotificationById(Long id);

    List<Notification> getUnreadNotifications(Long recipientId);

    void markedAsReadNotification(Long id);

    void notifyOrganizerEventApproved(Long organizerId, Event event);

    void notifyOrganizerEventRejected(Long organizerId, Event event, String rejectionComment);

    void notifyAdminsOnNewEvent(Event event);

    void deleteNotification(Long id);
}
