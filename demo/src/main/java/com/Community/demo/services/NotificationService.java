package com.Community.demo.services;

import com.Community.demo.model.Notification;
import java.util.List;


public interface NotificationService {
    Notification createNotification(Long userId, String title, String message);
    List<Notification> getNotificationsForUser(Long userId);
    void markAsRead(Long notificationId);
}
