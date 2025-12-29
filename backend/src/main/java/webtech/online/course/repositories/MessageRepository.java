package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webtech.online.course.models.MeetingMessage;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MeetingMessage, Long> {

    List<MeetingMessage> findBySessionIdOrderBySentAtAsc(Long sessionId);
}
