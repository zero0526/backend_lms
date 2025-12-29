package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webtech.online.course.models.MeetingSession;

@Repository
public interface SessionRepository extends JpaRepository<MeetingSession, Long> {

    MeetingSession findByMeetingIdAndStatus(Long meetingId, String status);

    MeetingSession findByRoomName(String roomName);
}
