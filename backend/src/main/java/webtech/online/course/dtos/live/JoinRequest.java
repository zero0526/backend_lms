package webtech.online.course.dtos.live;

import lombok.Data;

@Data
public class JoinRequest {
    private Long meetingId;
    private String alias;
}
