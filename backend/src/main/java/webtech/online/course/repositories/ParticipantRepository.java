package webtech.online.course.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webtech.online.course.models.MeetingParticipant;
import webtech.online.course.models.User;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<MeetingParticipant, Long> {

    MeetingParticipant findBySessionIdAndParticipantIdAndLeftAtIsNull(Long sessionId, Long userId);


    MeetingParticipant findFirstBySessionIdAndParticipantOrderByJoinedAtDesc(Long sessionId, User participant);

    List<MeetingParticipant> findBySessionId(Long sessionId);
}
