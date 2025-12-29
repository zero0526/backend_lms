package webtech.online.course.controllers;

import io.livekit.server.WebhookReceiver;
import livekit.LivekitEgress;
import livekit.LivekitWebhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.security.UserPrincipal;
import webtech.online.course.services.MeetingService;

import java.util.List;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final MeetingService meetingService;
    private final WebhookReceiver webhookReceiver;

    /**
     * API: POST /api/webhook
     * Chức năng: Nhận sự kiện từ LiveKit (User rớt mạng, User tắt tab, Ghi hình
     * xong).
     */
    @PostMapping(consumes = "application/webhook+json")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String body,
            @RequestHeader("Authorization") String authHeader) {
        LivekitWebhook.WebhookEvent event;
        try {
            // Xác thực chữ ký
            event = webhookReceiver.receive(body, authHeader);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Signature");
        }

        String eventType = event.getEvent();

        if ("participant_left".equals(eventType)) {
            // Lấy userId và roomName từ event
            String userId = event.getParticipant().getIdentity();
            String roomName = event.getRoom().getName();

            // Update DB
            meetingService.handleParticipantLeft(roomName, userId);
        } else if ("egress_ended".equals(eventType)) {
            log.info("start the egress ended event");
            var egress = event.getEgressInfo();
            if (egress != null) {
                String roomName = egress.getRoomName();
                long startedAt = egress.getStartedAt();
                long endedAt = egress.getEndedAt();
                long durationSeconds = (endedAt - startedAt) / 1000;

                List<LivekitEgress.FileInfo> files = egress.getFileResultsList();
                if (!files.isEmpty()) {
                    for (LivekitEgress.FileInfo fileInfo : files) {
                        String url = fileInfo.getLocation();
                        meetingService.handleRecordingEnded(roomName, url, durationSeconds);
                    }
                } else {
                    meetingService.handleRecordingEnded(roomName, null, durationSeconds);
                }
            }
        }

        return ResponseEntity.ok("ok");
    }
}
