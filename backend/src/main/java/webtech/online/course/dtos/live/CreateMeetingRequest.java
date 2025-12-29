package webtech.online.course.dtos.live;

import lombok.Data;

@Data
public class CreateMeetingRequest {
    private Long courseId;
    private String title;
    private String description;
}
