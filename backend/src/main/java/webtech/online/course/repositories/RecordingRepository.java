package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webtech.online.course.models.MeetingRecording;

import java.util.List;

@Repository
public interface RecordingRepository extends JpaRepository<MeetingRecording, Long> {

     List<MeetingRecording> findBySessionId(Long sessionId);
}
