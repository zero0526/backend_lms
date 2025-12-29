package webtech.online.course.controllers;

import lombok.extern.slf4j.Slf4j;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.live.JoinRequest;
import webtech.online.course.dtos.live.CreateMeetingRequest;
import webtech.online.course.dtos.live.RecordRequest;
import webtech.online.course.models.Meeting;
import webtech.online.course.models.MeetingSession;
import webtech.online.course.repositories.SessionRepository;
import webtech.online.course.security.UserPrincipal;
import webtech.online.course.services.LiveKitService;
import webtech.online.course.services.MeetingService;
import webtech.online.course.services.UserService;
import webtech.online.course.services.YoutubeService;
import webtech.online.course.repositories.RecordingRepository;
import webtech.online.course.models.MeetingRecording;
import webtech.online.course.dtos.Youtube.VideoUploadRequest;
import webtech.online.course.utils.CustomMultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/meeting")
@RequiredArgsConstructor
@Slf4j
public class MeetingController {

    private final LiveKitService liveKitService;
    private final MeetingService meetingService;
    private final UserService userService;
    private final SessionRepository sessionRepository;
    private final YoutubeService youtubeService;
    private final RecordingRepository recordingRepository;


    @PostMapping("/create")
    public ResponseEntity<?> createMeeting(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CreateMeetingRequest req) {
        Long userId = userPrincipal.getId();
        String url = meetingService.createMeeting(userId, req);
        return ResponseEntity.ok("Create new meeting successfully join meeting through %s".formatted(url));
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinMeeting(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody JoinRequest req) {
        if (req.getMeetingId() == null) {
            return ResponseEntity.badRequest().body("Meeting ID is required");
        }

        Long userId = userPrincipal.getId();

        Map<String, Object> response = meetingService.handleJoinRequest(
                req.getMeetingId(),
                userId,
                req.getAlias());

        return ResponseEntity.ok(response);
    }

    /**
     * API: GET /api/meeting/preview/{meetingId}
     * Chức năng: Lấy thông tin cuộc họp trước khi tham gia
     */
    @GetMapping("/preview/{meetingId}")
    public ResponseEntity<?> getPreviewInfo(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long meetingId) {
        Long userId = userPrincipal.getId();
        Map<String, Object> response = meetingService.getPreviewInfo(meetingId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * API: POST /api/public/meeting/record/start
     * Chức năng: Giảng viên bấm nút ghi hình.
     */
    @PostMapping("/record/start")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> startRecording(@RequestBody RecordRequest req) {
        if (req.getRoomName() == null) {
            return ResponseEntity.badRequest().body("Room name required");
        }

        String egressId = liveKitService.startRecording(req.getRoomName());

        if (egressId != null && !"error".equals(egressId)) {
            MeetingSession session = sessionRepository.findByRoomName(req.getRoomName());
            if (session != null) {
                session.setEgressId(egressId);
                sessionRepository.save(session);
            }
        }

        return ResponseEntity.ok(Map.of(
                "message", "Recording started",
                "egressId", egressId != null ? egressId : "error"));
    }

    /**
     * API: POST /api/public/meeting/record/stop
     * Chức năng: Dừng ghi hình thủ công.
     */
    @PostMapping("/record/stop")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> stopRecording(@RequestBody RecordRequest req) {
        if (req.getEgressId() == null || req.getEgressId().isEmpty()) {
            return ResponseEntity.badRequest().body("Egress ID required");
        }

        liveKitService.stopRecording(req.getRoomName(), req.getEgressId());

        MeetingSession session = sessionRepository.findByRoomName(req.getRoomName());
        if (session != null && req.getEgressId().equals(session.getEgressId())) {
            session.setEgressId(null);
            sessionRepository.save(session);
        }

        return ResponseEntity.ok(Map.of(
                "message", "Stop command sent",
                "info",
                "Recording will be finalized in 'recordings/' folder and then automatically uploaded to YouTube."));
    }

    @PostMapping("/record/upload-youtube/{recordingId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> uploadToYoutube(@PathVariable Long recordingId) throws IOException {
        MeetingRecording recording = recordingRepository.findById(recordingId)
                .orElseThrow(() -> new RuntimeException("Recording not found"));

        String fileUrl = recording.getUrl();
        if (fileUrl == null || fileUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Recording URL is empty");
        }

        File videoFile = new File(fileUrl);
        if (!videoFile.exists()) {
            // Try fallback mapping
            String mappingPath = fileUrl;
            if (fileUrl.startsWith("/out/")) {
                mappingPath = fileUrl.replace("/out/", "recordings/");
            }
            videoFile = new File(mappingPath);
            if (!videoFile.exists()) {
                videoFile = new File("/app/" + mappingPath);
            }
        }

        if (!videoFile.exists()) {
            return ResponseEntity.badRequest().body("Video file not found on server at: " + fileUrl);
        }

        CustomMultipartFile multipartFile = new CustomMultipartFile(
                "video",
                videoFile.getName(),
                "video/mp4",
                videoFile);

        VideoUploadRequest req = VideoUploadRequest.builder()
                .title("Meeting Recording - " + recording.getSession().getRoomName())
                .description("Recording of meeting session " + recording.getSession().getId())
                .categoryId("22")
                .privacyStatus("unlisted")
                .videoFile(multipartFile)
                .duration(recording.getDuration() != null ? recording.getDuration().getSeconds() : 0L)
                .build();

        var response = youtubeService.uploadVideo(req);

        // Update DB with YouTube URL
        recording.setUrl(response.videoUrl());
        recordingRepository.save(recording);
        log.info("Manually updated recording ID {} with YouTube URL: {}", recordingId, response.videoUrl());

        // Delete local file
        if (videoFile.delete()) {
            log.info("Deleted local file after manual upload: {}", videoFile.getAbsolutePath());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * API: GET /api/meeting/recordings/{sessionId}
     * Chức năng: Lấy danh sách video đã quay của một session.
     */
    @GetMapping("/recordings/{sessionId}")
    public ResponseEntity<?> getRecordings(@PathVariable Long sessionId) {
        return ResponseEntity.ok(recordingRepository.findBySessionId(sessionId));
    }
}
