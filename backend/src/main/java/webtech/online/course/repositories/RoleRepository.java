package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webtech.online.course.models.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
