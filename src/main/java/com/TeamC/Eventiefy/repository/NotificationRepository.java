package com.TeamC.Eventiefy.repository;

import com.TeamC.Eventiefy.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdAndIsReadFalse(Long userId);

    List<Notification> findByRecipientId(Long userId);
}