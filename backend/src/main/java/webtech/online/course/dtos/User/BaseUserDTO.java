package webtech.online.course.dtos.User;

public record BaseUserDTO(
        Long userId,
        String userName,
        String email,
        String role,
        String avatar,
        String accessToken,
        String refreshToken
) {
}
