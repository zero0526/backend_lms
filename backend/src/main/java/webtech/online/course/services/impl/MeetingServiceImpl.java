package webtech.online.course.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webtech.online.course.configs.FrontendConfig;
import webtech.online.course.configs.LiveKitConfig;
import webtech.online.course.dtos.Youtube.VideoUploadRequest;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.models.*;
import webtech.online.course.repositories.*;
import webtech.online.course.services.LiveKitService;
import webtech.online.course.services.MeetingService;
import webtech.online.course.services.YoutubeService;
import webtech.online.course.utils.CustomMultipartFile;
import webtech.online.course.services.PostService;
import webtech.online.course.dtos.post.CreatePostDTO;
import webtech.online.course.dtos.live.CreateMeetingRequest;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepo;
    private final SessionRepository sessionRepo;
    private final ParticipantRepository participantRepo;
    private final UserRepository userRepo;
    private final LiveKitService liveKitService;
    private final LiveKitConfig liveKitConfig;
    private final RecordingRepository recordingRepo;
    private final YoutubeService youtubeService;
    private final PostService postService;
    private final CourseRepository courseRepo;
    private final FrontendConfig frontendConfig;
    private final webtech.online.course.services.NotificationService notificationService;

    /**
     * Logic: Xử lý khi người dùng yêu cầu tham gia (API /join)
     */
    @Override
    @Transactional
    public Map<String, Object> handleJoinRequest(Long meetingId, Long userId, String alias) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new BaseError("Not Found user has id=%d".formatted(userId)));

        Meeting meeting;
        if (meetingId == null) {
            meeting = Meeting.builder()
                    .title("Quick Meeting - " + LocalDateTime.now().toString())
                    .originator(user)
                    .recordOption(false)
                    .defaultDurationMinutes(60)
                    .build();
            meeting = meetingRepo.save(meeting);
            meetingId = meeting.getId();
            log.info("Created new meeting with ID: {}", meetingId);
        } else {
            meeting = findById(meetingId);
        }

        MeetingSession session = sessionRepo.findByMeetingIdAndStatus(meetingId, "live");
        if (session == null) {
            session = new MeetingSession();
            session.setMeeting(meeting);
            session.setStatus("live");
            session = sessionRepo.save(session); // Save first to get ID

            session.setRoomName("MEETING_" + session.getId());
            session = sessionRepo.save(session);
        } else if (session.getRoomName() == null || session.getRoomName().equals("MEETING_null")) {
            session.setRoomName("MEETING_" + session.getId());
            session = sessionRepo.save(session);
        }

        MeetingParticipant part = new MeetingParticipant();
        part.setSession(session);
        part.setParticipant(user);
        part.setJoinedAt(LocalDateTime.now());
        participantRepo.save(part);

        boolean isTeacher = user.getRole().getName().toUpperCase().contains("TEACHER");
        String displayName = (alias != null && !alias.trim().isEmpty()) ? alias : user.getFullName();
        String token = liveKitService.createToken(session.getRoomName(), String.valueOf(user.getId()), displayName,
                isTeacher, user.getPictureUrl());

        Map<String, Object> result = new HashMap<>();

        result.put("serverUrl", liveKitConfig.getUrl());
        result.put("wsUrl", liveKitConfig.getUrl());

        result.put("roomName", session.getRoomName());
        result.put("sessionId", session.getId());

        log.info("User {} joining session {} with room name: {}", userId, session.getId(), session.getRoomName());

        result.put("role", user.getRole().getName().substring(5));
        result.put("token", token);
        result.put("name", user.getFullName());
        result.put("userId", user.getId());
        result.put("avatar", user.getPictureUrl());
        result.put("egressId", session.getEgressId());

        return result;
    }

    @Override
    @Transactional
    public String createMeeting(Long userId, CreateMeetingRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new BaseError("Not Found user has id=%d".formatted(userId)));

        Course course = null;
        if (request.getCourseId() != null) {
            course = courseRepo.findById(request.getCourseId())
                    .orElseThrow(() -> new BaseError("Not Found course has id=%d".formatted(request.getCourseId())));
        }

        Meeting meeting = Meeting.builder()
                .title(request.getTitle())
                .originator(user)
                .course(course)
                .recordOption(true)
                .defaultDurationMinutes(60)
                .build();

        meeting = meetingRepo.save(meeting);
        String url = "%s/meeting/room/%d".formatted(frontendConfig.getUri(), meeting.getId());

        // Auto post to course if course is present
        if (course != null) {
            String content = "New meeting created: " + meeting.getTitle() + "\n" +
                    (request.getDescription() != null ? request.getDescription() : "");
            content += "\n Please join through below url \n %s".formatted(url);
            CreatePostDTO postDTO = CreatePostDTO.builder()
                    .courseId(course.getId())
                    .title("Meeting Announcement: " + meeting.getTitle())
                    .content(content)
                    .build();

            try {
                postService.createPost(userId, postDTO);

                if (course.getEnrollments() != null) {
                    for (Enrollment enrollment : course.getEnrollments()) {
                        if (enrollment.getUser() != null && !enrollment.getUser().getId().equals(userId)) {
                            notificationService.createNotification(
                                    enrollment.getUser().getId(),
                                    "New Meeting Scheduled",
                                    "A new live session has been scheduled for " + course.getTitle() + ": "
                                            + meeting.getTitle(),
                                    "/meeting/room/" + meeting.getId());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Failed to auto-post/notify meeting info: {}", e.getMessage());
            }
        }
        return url;
    }

    @Override
    public Map<String, Object> getPreviewInfo(Long meetingId, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new BaseError("Not Found user has id=%d".formatted(userId)));

        Meeting meeting = findById(meetingId);

        Map<String, Object> result = new HashMap<>();
        result.put("meetingId", meeting.getId());
        result.put("meetingTitle", meeting.getTitle());
        result.put("defaultName", user.getFullName());
        result.put("role", user.getRole().getName());
        result.put("userId", user.getId());

        return result;
    }

    /**
     * Logic: Xử lý Webhook khi người dùng rời phòng
     */
    @Override
    @Transactional
    public void handleParticipantLeft(String roomName, String userIdStr) {
        // Parse userId từ chuỗi identity
        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            log.warn("Ignored participant_left for non-numeric identity (likely Egress/Recorder): {}", userIdStr);
            return;
        }

        // Tìm Session dựa trên roomName
        MeetingSession session = sessionRepo.findByRoomName(roomName);

        if (session != null) {
            // Tìm record tham gia CẦN UPDATE (record nào có joined_at gần nhất và left_at
            // đang null)
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new BaseError("Not Found user has id=%d".formatted(userId)));
            // Giả sử có method findLastActiveSession
            MeetingParticipant part = participantRepo.findFirstBySessionIdAndParticipantOrderByJoinedAtDesc(
                    session.getId(),
                    user);

            // Nếu tìm thấy, cập nhật left_at = Now()
            if (part != null && part.getLeftAt() == null) {
                part.setLeftAt(LocalDateTime.now());
                participantRepo.save(part);
            }
        }
    }

    @Override
    @Transactional
    public void handleRecordingEnded(String roomName, String fileUrl, long durationSeconds) {
        MeetingSession session = sessionRepo.findByRoomName(roomName);
        if (session != null) {
            // Clear egressId when recording ends
            session.setEgressId(null);
            sessionRepo.save(session);

            MeetingRecording rec = new MeetingRecording();
            rec.setSession(session);
            rec.setUrl(fileUrl);
            rec.setDuration(Duration.ofSeconds(durationSeconds));
            rec = recordingRepo.save(rec);

            // Upload to YouTube asynchronously
            if (fileUrl != null && !fileUrl.isEmpty()) {
                final Long recordingId = rec.getId();
                final String finalRoomName = session.getRoomName();
                final Long finalSessionId = session.getId();

                if (TransactionSynchronizationManager.isActualTransactionActive()) {
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            triggerAsyncUpload(recordingId, fileUrl, finalRoomName, finalSessionId, durationSeconds);
                        }
                    });
                } else {
                    triggerAsyncUpload(recordingId, fileUrl, finalRoomName, finalSessionId, durationSeconds);
                }
            }
        }
    }

    private void triggerAsyncUpload(Long recordingId, String fileUrl, String roomName, Long sessionId,
            long durationSeconds) {
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                uploadToYoutube(recordingId, fileUrl, roomName, sessionId, durationSeconds);
            } catch (Exception e) {
                log.error("Async YouTube upload failed: {}", e.getMessage());
            }
        });
    }

    @Override
    public Meeting findById(Long id) {
        return meetingRepo.findById(id)
                .orElseThrow(() -> new BaseError("Not found the meeting has id=%d".formatted(id)));
    }

    @Transactional
    public void uploadToYoutube(Long recordingId, String fileUrl, String roomName, Long sessionId,
            long durationSeconds) {
        File videoFile = null;
        try {
            log.info("Preparing to upload to YouTube: {}", fileUrl);

            // Translate /out/ path from LiveKit to recordings/ for the App container
            String actualPath = fileUrl;
            if (fileUrl.startsWith("/out/")) {
                actualPath = fileUrl.replace("/out/", "recordings/");
            }

            File localFile = new File(actualPath);
            // Ensure parent directory exists for local environments
            if (localFile.getParentFile() != null && !localFile.getParentFile().exists()) {
                localFile.getParentFile().mkdirs();
            }

            if (localFile.exists()) {
                videoFile = localFile;
            } else {
                // Try absolute path as fallback
                File absFile = new File("/app/" + actualPath);
                if (absFile.exists()) {
                    videoFile = absFile;
                } else {
                    log.warn("File not found locally at: {} or {}", actualPath, absFile.getAbsolutePath());
                    return;
                }
            }

            CustomMultipartFile multipartFile = new CustomMultipartFile(
                    "video",
                    videoFile.getName(),
                    "video/mp4",
                    videoFile);

            VideoUploadRequest req = VideoUploadRequest.builder()
                    .title("Meeting " + roomName)
                    .description("Recording of meeting session " + sessionId)
                    .categoryId("22") // People & Blogs
                    .privacyStatus("unlisted")
                    .videoFile(multipartFile)
                    .duration(durationSeconds)
                    .build();

            var response = youtubeService.uploadVideo(req);
            log.info("YouTube upload successful for: {}. URL: {}", roomName, response.videoUrl());

            // Update DB with YouTube URL
            MeetingRecording rec = recordingRepo.findById(recordingId).orElse(null);
            if (rec != null) {
                rec.setUrl(response.videoUrl());
                recordingRepo.save(rec);
                log.info("Updated recording ID {} with YouTube URL: {}", recordingId, response.videoUrl());

                // Auto post recording to course
                if (rec.getSession() != null && rec.getSession().getMeeting() != null
                        && rec.getSession().getMeeting().getCourse() != null) {
                    Long courseId = rec.getSession().getMeeting().getCourse().getId();
                    Long originatorId = rec.getSession().getMeeting().getOriginator().getId();
                    String title = "Meeting Recording: " + rec.getSession().getMeeting().getTitle();
                    String content = "Recording for session is now available: " + response.videoUrl();

                    CreatePostDTO postDTO = CreatePostDTO.builder()
                            .courseId(courseId)
                            .title(title)
                            .content(content)
                            .build();

                    try {
                        postService.createPost(originatorId, postDTO);
                        log.info("Auto-posted recording to course {}", courseId);
                    } catch (Exception e) {
                        log.error("Failed to auto-post recording: {}", e.getMessage());
                    }
                }

                // Delete local file after successful upload and DB update
                if (videoFile.delete()) {
                    log.info("Deleted local file: {}", videoFile.getAbsolutePath());
                } else {
                    log.warn("Failed to delete local file: {}", videoFile.getAbsolutePath());
                }
            }

        } catch (Exception e) {
            log.error("Failed to upload to YouTube: {}", e.getMessage());
        }
    }
}
