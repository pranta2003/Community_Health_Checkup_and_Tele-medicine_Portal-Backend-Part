package com.Community.demo.controller;

import com.Community.demo.model.Notification;
import com.Community.demo.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Simple controller to fetch and mark notifications.
 *
 * GET /api/notifications?userId=123   -> list notifications for a user
 * POST /api/notifications/mark-read  -> mark a notification read
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // List notifications for given user id
    @GetMapping
    public ResponseEntity<List<Notification>> listNotifications(@RequestParam("userId") Long userId) {
        List<Notification> list = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(list);
    }

    // Mark a notification as read
    @PostMapping("/mark-read")
    public ResponseEntity<?> markRead(@RequestParam("id") Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("marked");
    }
}
