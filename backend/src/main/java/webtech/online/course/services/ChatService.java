package webtech.online.course.services;

import webtech.online.course.dtos.live.ChatMessageResponse;

import java.util.List;

public interface ChatService {
    ChatMessageResponse saveMessage(Long sessionId, Long userId, String content, Long replyToMessageId);

    List<ChatMessageResponse> getHistory(Long sessionId);
}
