package webtech.online.course.dtos.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import webtech.online.course.enums.Gender;
import webtech.online.course.models.UserProfile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInformationDTO {
    Long userId;
    String fullName;
    String pictureUrl;
    LocalDateTime createdAt;
    LocalDateTime lastLogin;
    String phoneNumber;
    Gender gender;
    String address;
    String bio;
}

