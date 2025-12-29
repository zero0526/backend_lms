package webtech.online.course.dtos.User;

import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public record LastLoginDTO(
        LocalDateTime lastLogin,
        Long  userId
) {
}
