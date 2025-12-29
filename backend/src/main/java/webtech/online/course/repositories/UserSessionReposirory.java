package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import webtech.online.course.models.UserSession;

import java.util.List;
@Repository
public interface UserSessionReposirory extends JpaRepository<UserSession, Long> {
    @Query(
            value = "SELECT * FROM user_sessions " +   // <== space ở cuối
                    "WHERE user_id = :userId " +       // <== space + tên param khớp
                    "AND revoked = false " +
                    "AND expires_at > NOW()",
            nativeQuery = true
    )
    List<UserSession> findActiveSessionByUserId(@Param("userId") Long userId);
}
