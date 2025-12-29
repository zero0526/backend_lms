package webtech.online.course.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_rotations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "old_refresh_token")
    private String oldRefreshToken;

    @Column(name = "new_refresh_token")
    private String newRefreshToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private UserSession userSession;

    @Column(name = "rotated_at")
    @Builder.Default
    private LocalDateTime rotatedAt = LocalDateTime.now();
}
