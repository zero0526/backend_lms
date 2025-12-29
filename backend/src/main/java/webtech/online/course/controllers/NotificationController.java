package webtech.online.course.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.NotificationDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.exceptions.DefaultResponse;
import webtech.online.course.exceptions.WrapperResponse;
import webtech.online.course.services.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<WrapperResponse> getUserNotifications(
            @PathVariable Long userId, @RequestParam Integer page, @RequestParam Integer size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending());
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userId, pageable);

        return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), notifications));
    }

    @GetMapping("/user/next-page/{userId}")
    public ResponseEntity<WrapperResponse> getNextPageUserNotifications(
            @PathVariable Long userId, @RequestParam LocalDateTime lastCreatedAt, @RequestParam Long notifyID,
            @RequestParam Integer size) {
        Pageable pageable = PageRequest.of(
                0, size);
        List<NotificationDTO> notifications = notificationService.getNextPageUserNotifications(userId, lastCreatedAt,
                notifyID, pageable);

        return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), notifications));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<WrapperResponse> getUnreadNotifications(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), notifications));
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<WrapperResponse> getUnreadCount(@PathVariable Long userId) {
        Long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), count));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long notificationId) {
        try {
            NotificationDTO notification = notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(notification);
        } catch (Exception ex) {
            throw new BaseError(ex.getMessage());
        }
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<DefaultResponse> markAllAsRead(@PathVariable Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(new DefaultResponse(HttpStatus.OK.value()));
        } catch (Exception ex) {
            throw new BaseError(ex.getMessage());
        }
    }
}
