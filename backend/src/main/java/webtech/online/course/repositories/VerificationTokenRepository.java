package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webtech.online.course.models.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
}
