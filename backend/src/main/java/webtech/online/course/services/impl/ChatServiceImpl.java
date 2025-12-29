package webtech.online.course.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webtech.online.course.dtos.live.ChatMessageResponse;
import webtech.online.course.models.MeetingMessage;
import webtech.online.course.models.MeetingSession;
import webtech.online.course.models.User;
import webtech.online.course.repositories.MessageRepository;
import webtech.online.course.repositories.SessionRepository;
import webtech.online.course.repositories.UserRepository;
import webtech.online.course.services.ChatService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public ChatMessageResponse saveMessage(Long sessionId, Long userId, String content, Long replyToMessageId) {
        MeetingSession session = sessionRepository.getReferenceById(sessionId);
        User sender = null;
        if (userId != null) {
            sender = userRepository.getReferenceById(userId);
        }

        MeetingMessage msg = new MeetingMessage();
        msg.setSession(session);
        msg.setSender(sender);
        msg.setContent(content);
        msg.setSentAt(LocalDateTime.now());

        if (replyToMessageId != null) {
            MeetingMessage parent = messageRepository.findById(replyToMessageId).orElse(null);
            msg.setParentMessage(parent);
        }

        msg = messageRepository.save(msg);
        return ChatMessageResponse.parser(msg);
    }

    @Override
    @Transactional
    public List<ChatMessageResponse> getHistory(Long sessionId) {
        return messageRepository.findBySessionIdOrderBySentAtAsc(sessionId).stream().map(ChatMessageResponse::parser)
                .toList();
    }
}
