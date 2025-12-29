package webtech.online.course.services;

import webtech.online.course.models.Meeting;

import java.util.Map;

public interface MeetingService {
    Map<String, Object> handleJoinRequest(Long meetingId, Long userId, String alias);

    String createMeeting(Long userId, webtech.online.course.dtos.live.CreateMeetingRequest request);

    Map<String, Object> getPreviewInfo(Long meetingId, Long userId);

    void handleParticipantLeft(String roomName, String userIdStr);

    void handleRecordingEnded(String roomName, String fileUrl, long duration);

    Meeting findById(Long id);
}
