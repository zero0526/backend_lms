package webtech.online.course.dtos.live;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MeetingResponse {
    private Long id;
    private String title;
    private String description;

    private UserResponse owner;

    private Boolean isRecordingEnabled;
    private LocalDateTime createdAt;

    private int totalSessions;
}
