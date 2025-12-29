package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import webtech.online.course.controllers.UserController;
import webtech.online.course.domains.FileInfo;
import webtech.online.course.dtos.Drive.DriveRequest;
import webtech.online.course.dtos.User.LastLoginDTO;
import webtech.online.course.dtos.User.UpdateUserRequest;
import webtech.online.course.dtos.User.UserInformationDTO;
import webtech.online.course.enums.Gender;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.models.User;
import webtech.online.course.models.UserProfile;
import webtech.online.course.repositories.UserProfileRepository;
import webtech.online.course.services.DriveService;
import webtech.online.course.services.UserProfileService;
import webtech.online.course.services.UserService;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserService userService;
    private final DriveService driveService;

    public UserProfile findById(Long userId) {
        return userProfileRepository.findById(userId).orElseGet(() -> {
            User user = userService.findById(userId);
            UserProfile userProfile = UserProfile.builder()
                    .user(user)
                    .build();
            return userProfileRepository.save(userProfile);
        });
    }

    @Override
    @Transactional
    public void updateLastLogin(LastLoginDTO lastLoginDTO) {
        User user = userService.findById(lastLoginDTO
                .userId());
        UserProfile userProfile = userProfileRepository.findById(lastLoginDTO.userId())
                .orElse(UserProfile.builder().user(user).build());
        userProfile.setLastLogin(lastLoginDTO.lastLogin());
        userProfileRepository.save(userProfile);
    }

    @Override
    @Transactional
    public UserInformationDTO getDetailsInformation(Long userId) {
        UserProfile userP = findById(userId);
        User u = userP.getUser();
        return new UserInformationDTO(userId, u.getFullName(), u.getPictureUrl(), u.getCreatedAt(),
                userP.getLastLogin(), userP.getPhoneNumber(), userP.getGender(), userP.getAddress(), userP.getBio());
    }

    @Override
    @Transactional
    public UserInformationDTO updateUser(Long userId, UpdateUserRequest req) throws IOException {

        UserProfile userP = findById(userId);
        User u = userP.getUser();

        // update bảng User
        if (req.getFullName() != null) {
            u.setFullName(req.getFullName());
        }
        if (req.getPicture() != null && !req.getPicture().isEmpty()) {
            FileInfo avatar = driveService.uploadFile(new DriveRequest(req.getPicture()));
            u.setPictureUrl(avatar.urlUploaded());
        }

        // update bảng UserProfile
        if (req.getPhoneNumber() != null) {
            userP.setPhoneNumber(req.getPhoneNumber());
        }
        if (req.getGender() != null) {
            userP.setGender(req.getGender());
        }
        if (req.getAddress() != null) {
            userP.setAddress(req.getAddress());
        }
        if (req.getBio() != null) {
            userP.setBio(req.getBio());
        }

        userService.save(u);
        userProfileRepository.save(userP);

        return new UserInformationDTO(
                userId,
                u.getFullName(),
                u.getPictureUrl(),
                u.getCreatedAt(),
                userP.getLastLogin(),
                userP.getPhoneNumber(),
                userP.getGender(),
                userP.getAddress(),
                userP.getBio());
    }

}
