package webtech.online.course.dtos.User;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
}
