package webtech.online.course.utils;


import webtech.online.course.dtos.live.SessionResponse;
import webtech.online.course.models.MeetingSession;

public class MapperUtils {
    public static SessionResponse fromEntity(MeetingSession session) {
        return SessionResponse.builder()
                .id(session.getId())
                .meetingId(session.getMeeting().getId())
                .meetingTitle(session.getMeeting().getTitle())
                .roomName(session.getRoomName())
                .status(session.getStatus())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .build();
    }
}
