package com.Community.demo.services.impl;

import com.Community.demo.model.Notification;
import com.Community.demo.repository.NotificationRepository;
import com.Community.demo.services.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;

    public NotificationServiceImpl(NotificationRepository repo) {
        this.repo = repo;
    }

    @Override
    public Notification createNotification(Long userId, String title, String message) {
        Notification n = new Notification(userId, title, message);
        return repo.save(n);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Optional<Notification> opt = repo.findById(notificationId);
        if (opt.isPresent()) {
            Notification n = opt.get();
            n.setRead(true);
            repo.save(n);
        }
    }
}
