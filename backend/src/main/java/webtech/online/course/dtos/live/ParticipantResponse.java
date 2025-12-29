package webtech.online.course.dtos.live;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ParticipantResponse {
    private Long id;

    private Long userId;
    private String userName;
    private String userRole;

    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;

    private Long durationSeconds;
}
