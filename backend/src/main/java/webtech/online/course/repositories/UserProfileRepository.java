package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webtech.online.course.models.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
