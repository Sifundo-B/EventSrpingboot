package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.Event;
import com.TeamC.Eventiefy.entity.Notification;
import com.TeamC.Eventiefy.enums.NotificationType;
import com.TeamC.Eventiefy.enums.Role;
import com.TeamC.Eventiefy.repository.EventRepository;
import com.TeamC.Eventiefy.repository.NotificationRepository;
import com.TeamC.Eventiefy.repository.UserRepo;
import com.TeamC.Eventiefy.user.User;
import com.TeamC.Eventiefy.util.NotificationFormatter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationServiceInit {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    @Override
    public Notification createNotification(Notification notification) {
        notification.setTimestamp(LocalDate.now());
        notification.setRead(false);

        // Fetch the recipient user and set it
        User recipient = userRepository.findById(notification.getRecipient().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        notification.setRecipient(recipient);

        // Fetch the event and set it
        Event event = eventRepository.findById(notification.getEvent().getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
        notification.setEvent(event);

        // Save notification
        Notification savedNotification = notificationRepository.save(notification);

        // Publish to Redis and WebSocket
        redisTemplate.convertAndSend(topic.getTopic(), savedNotification);
        template.convertAndSend("/topic/notifications/" + recipient.getId(), savedNotification);

        return savedNotification;
    }


    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByRecipientId(userId);
    }

    @Override
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalse(userId);
    }

    @Override
    public void markedAsReadNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void notifyOrganizerEventApproved(Long eventId, Event event) {
        Event events = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        User creator = event.getOrganizer(); // a method to get the creator

        String message = NotificationFormatter.formatNotificationMessage(NotificationType.EVENT_APPROVAL, event, creator.getFirstName(), null);

        Notification notification = new Notification();
        notification.setType(NotificationType.EVENT_APPROVAL);
        notification.setMessage(message);
        notification.setRecipient(creator);
        notification.setEvent(event);

        createNotification(notification);
    }

    @Override
    public void notifyOrganizerEventRejected(Long eventId, Event event, String rejectionCommentJson) {
        Event eventToUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User creator = userRepository.findById(eventToUpdate.getOrganizer().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        JsonObject jsonObject = JsonParser.parseString(rejectionCommentJson).getAsJsonObject();
        String rejectionComment = jsonObject.get("rejectionComment").getAsString();

        String message = NotificationFormatter.formatNotificationMessage(NotificationType.EVENT_REJECTION, eventToUpdate, creator.getFirstName(), rejectionComment);

        Notification notification = new Notification();
        notification.setType(NotificationType.EVENT_REJECTION);
        notification.setMessage(message);
        notification.setRecipient(creator);
        notification.setEvent(eventToUpdate);
        notification.setRejectionComment(rejectionComment); // Save the rejection comment

        createNotification(notification);
    }

    @Override
    public void notifyAdminsOnNewEvent(Event event) {
        List<User> recipients = userRepository.findByRole(Role.Admin); // Fetch all admins

        for (User recipient : recipients) {
            String message = NotificationFormatter.formatNotificationMessage(NotificationType.EVENT_CREATION, event, null, null);
            Notification notification = new Notification();
            notification.setType(NotificationType.EVENT_CREATION);
            notification.setMessage(message);
            notification.setRecipient(recipient);
            notification.setEvent(event);
            createNotification(notification);
        }
    }

    @Override
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification with id " + id + " not found"));
        notificationRepository.delete(notification);
    }
}