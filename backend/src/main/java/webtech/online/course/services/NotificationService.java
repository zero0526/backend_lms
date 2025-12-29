package webtech.online.course.services;

import org.springframework.data.domain.Pageable;
import webtech.online.course.dtos.NotificationDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {
    NotificationDTO createNotification(Long userId, String title, String message, String linkUrl);

    List<NotificationDTO> getUserNotifications(Long userId, Pageable pageable);

    List<NotificationDTO> getUnreadNotifications(Long userId);

    List<NotificationDTO> getNextPageUserNotifications(Long userId, LocalDateTime lastCreatedAt, Long notifyId,
            Pageable pageable);

    NotificationDTO markAsRead(Long notificationId);

    void markAllAsRead(Long userId);

    Long getUnreadCount(Long userId);
}
