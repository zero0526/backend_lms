package webtech.online.course.controllers;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.live.ChatMessageResponse;
import webtech.online.course.dtos.live.ChatRequest;
import webtech.online.course.models.MeetingSession;
import webtech.online.course.repositories.SessionRepository;
import webtech.online.course.security.UserPrincipal;
import webtech.online.course.services.ChatService;
import webtech.online.course.services.LiveKitService;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final LiveKitService liveKitService;
    private final SessionRepository sessionRepository; // Need to look up roomName
    private final Gson gson;

    /**
     * API: POST /api/chat/send
     * Chức năng: Nhận tin nhắn, lưu DB, rồi broadcast cho mọi người.
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                         @RequestBody ChatRequest req) {
        if (req.getContent() == null || req.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Content required");
        }

        Long userId = userPrincipal.getId();

        // 1. Save to DB and get full response object
        ChatMessageResponse fullResponse = chatService.saveMessage(req.getSessionId(), userId,
                req.getContent(), req.getReplyToMessageId());

        // 2. Broadcast via LiveKit
        MeetingSession session = sessionRepository.findById(req.getSessionId()).orElse(null);
        if (session != null) {
            log.info("Broadcasting chat message to room: {} (Session: {})", session.getRoomName(), session.getId());
            liveKitService.sendDataToRoom(session.getRoomName(), gson.toJson(fullResponse));
        } else {
            log.warn("Could not find session with ID: {} for broadcasting", req.getSessionId());
        }

        return ResponseEntity.ok(fullResponse);
    }

    /**
     * API: GET /api/chat/history/{sessionId}
     * Chức năng: Lấy lịch sử chat khi user vào sau hoặc load lại trang.
     */
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<?> getChatHistory(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                            @PathVariable Long sessionId) {
        return ResponseEntity.ok(chatService.getHistory(sessionId));
    }
}

