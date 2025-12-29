package webtech.online.course.dtos.live;

import lombok.Builder;
import lombok.Data;
import webtech.online.course.models.MeetingMessage;

@Data
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long sessionId;
    private Long parentId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;

    private String content;
    private String sentAt;

    public static ChatMessageResponse parser(MeetingMessage message) {
        Long par = message.getParentMessage() != null ? message.getParentMessage().getId() : null;
        return ChatMessageResponse.builder()
                .id(message.getId())
                .sessionId(message.getSession().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFullName())
                .senderAvatar(message.getSender().getPictureUrl())
                .content(message.getContent())
                .parentId(par)
                .sentAt(message.getSentAt() != null ? message.getSentAt().toString() : null)
                .build();
    }
}
