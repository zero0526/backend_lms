package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webtech.online.course.models.Segment;

@Repository
public interface SegmentRepository extends JpaRepository<Segment, Long> {
}
