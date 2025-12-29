package webtech.online.course.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import webtech.online.course.configs.Configs;

import java.time.LocalDateTime;

@Table(name = "user_sessions")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "revoked")
    @Builder.Default
    private Boolean revoked = Boolean.FALSE;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "last_active_at")
    @Builder.Default
    private LocalDateTime lastActiveAt = LocalDateTime.now();

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (expiresAt == null) {
            Long expireSeconds = Configs.getExpireSeconds();
            expiresAt = LocalDateTime.now().plusSeconds(expireSeconds);
        }
    }
}
