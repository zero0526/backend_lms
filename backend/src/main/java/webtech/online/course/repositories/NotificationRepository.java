package webtech.online.course.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import webtech.online.course.models.Notification;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    Long countByUserIdAndIsReadFalse(Long userId);

    @Query("""
      SELECT n FROM Notification n
      WHERE n.user.id = :userId
        AND ( n.createdAt < :lastCreatedAt
              OR (n.createdAt = :lastCreatedAt AND n.id < :lastId) )
      ORDER BY n.createdAt DESC, n.id DESC
    """)
    List<Notification> findNextPageUserNotification(@Param("") Long userId, @Param("lastCreatedAt") LocalDateTime lastCreatedAt, Long notifyId, Pageable pageable);
}
