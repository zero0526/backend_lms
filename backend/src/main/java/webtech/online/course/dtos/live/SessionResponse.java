package webtech.online.course.dtos.live;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SessionResponse {
    private Long id;

    private Long meetingId;
    private String meetingTitle;

    private String roomName;
    private String status;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
