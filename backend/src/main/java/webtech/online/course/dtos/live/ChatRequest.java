package webtech.online.course.dtos.live;

import lombok.Data;

@Data
public class ChatRequest {
    private Long sessionId;
    private String content;
    private Long replyToMessageId;
}
