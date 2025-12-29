package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webtech.online.course.models.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
}
