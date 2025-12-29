package webtech.online.course.dtos.User;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import webtech.online.course.enums.Gender;

@Data
public class UpdateUserRequest {
    private String fullName;
    private MultipartFile picture;
    private String phoneNumber;
    private Gender gender;
    private String address;
    private String bio;
}
