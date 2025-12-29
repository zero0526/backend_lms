package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webtech.online.course.models.OAuthProvider;

public interface OAuthProviderRepository extends JpaRepository<OAuthProvider, Long> {
}
