package webtech.online.course.services;

import webtech.online.course.dtos.User.LastLoginDTO;
import webtech.online.course.dtos.User.UpdateUserRequest;
import webtech.online.course.dtos.User.UserInformationDTO;

import java.io.IOException;

public interface UserProfileService {
    public void updateLastLogin(LastLoginDTO lastLoginDTO);
    public UserInformationDTO getDetailsInformation(Long userId);
    public UserInformationDTO updateUser(Long userId, UpdateUserRequest req)throws IOException;
}
