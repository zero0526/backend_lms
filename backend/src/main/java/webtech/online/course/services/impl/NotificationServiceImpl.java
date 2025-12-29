package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import webtech.online.course.configs.FrontendConfig;
import webtech.online.course.dtos.NotificationDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.models.Notification;
import webtech.online.course.models.User;
import webtech.online.course.repositories.NotificationRepository;
import webtech.online.course.repositories.UserRepository;
import webtech.online.course.services.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;
    private final FrontendConfig frontendConfig;

    @Override
    @Transactional
    public NotificationDTO createNotification(Long userId, String title, String message, String linkUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseError(404, "User not found with id=" + userId));
        String uri = frontendConfig.getUri();
        if (!linkUrl.contains(uri))
            linkUrl = uri + linkUrl;
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .linkUrl(linkUrl)
                .isRead(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        NotificationDTO dto = mapToDTO(savedNotification);
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, dto);

        return dto;
    }

    // get with paging
    @Override
    public List<NotificationDTO> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .stream().map(this::mapToDTO).toList();
    }

    @Override
    public List<NotificationDTO> getNextPageUserNotifications(Long userId, LocalDateTime lastCreatedAt, Long notifyId,
            Pageable pageable) {
        return notificationRepository.findNextPageUserNotification(userId, lastCreatedAt, notifyId, pageable)
                .stream().map(this::mapToDTO).toList();
    }

    // mark read
    @Override
    @Transactional
    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BaseError(404, "Notification not found with id=" + notificationId));

        notification.setIsRead(true);
        return mapToDTO(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        unreadNotifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    // unread
    @Override
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToDTO).toList();
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .linkUrl(notification.getLinkUrl())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
