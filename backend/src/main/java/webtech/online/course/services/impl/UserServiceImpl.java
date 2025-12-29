package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import webtech.online.course.configs.FrontendConfig;
import webtech.online.course.dtos.OAuth2UserInfo;
import webtech.online.course.dtos.RegisterRequest;
import webtech.online.course.dtos.User.ForgotPasswordRequest;
import webtech.online.course.dtos.User.UpdatePasswordRequest;
import webtech.online.course.enums.AuthProvider;
import webtech.online.course.enums.UserStatus;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.exceptions.ErrorResponse;
import webtech.online.course.models.Role;
import webtech.online.course.models.User;
import webtech.online.course.models.VerificationToken;
import webtech.online.course.repositories.UserRepository;
import webtech.online.course.repositories.VerificationTokenRepository;
import webtech.online.course.repositories.UserProfileRepository;
import webtech.online.course.models.UserProfile;
import webtech.online.course.services.EmailService;
import webtech.online.course.services.UserService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final RoleServiceImpl roleService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final FrontendConfig frontendConfig;

    @Override
    public User registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email has already existed");
        }

        Role role = roleService.findOrCreateRole(request.getRoleName());
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(hashedPassword)
                .fullName(request.getFullName())
                .role(role)
                .status(UserStatus.LOCKED)
                .build();
        user = userRepository.save(user);

        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .build();
        userProfileRepository.save(userProfile);

        return user;
    }

    @Override
    public User firstOAuth(OAuth2UserInfo oAuth2UserInfo, String roleName, String providerId) {
        if (userRepository.findByEmail(oAuth2UserInfo.getEmail()).isPresent()) {
            throw new RuntimeException("Email has already existed");
        }

        Role role = roleService.findOrCreateRole(roleName);
        AuthProvider authProvider = switch (providerId.toLowerCase()) {
            case "google" -> AuthProvider.GOOGLE;
            case "github" -> AuthProvider.GITHUB;
            case "facebook" -> AuthProvider.FACEBOOK;
            default -> AuthProvider.LOCAL;
        };

        User user = User.builder()
                .email(oAuth2UserInfo.getEmail())
                .fullName(oAuth2UserInfo.getName())
                .pictureUrl(oAuth2UserInfo.getImageUrl())
                .role(role)
                .providerUserId(oAuth2UserInfo.getUserProviderId())
                .authProvider(authProvider)
                .build();
        user = userRepository.save(user);

        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .build();
        userProfileRepository.save(userProfile);

        return user;
    }

    @Override
    public VerificationToken createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ErrorResponse(400, "not found user has id %d".formatted(id), "UserFindById"));
    }

    @Override
    @Transactional
    public User confirmOriginalLogin(String email, String role) {
        User user = findByEmail(email);
        if (user.getRole().getName().equals(role) && user.getAuthProvider().equals(AuthProvider.LOCAL))
            return user;
        return null;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Not found the user has email, role is provided"));
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest req) {
        User user = findById(userId);

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void requestPasswordReset(ForgotPasswordRequest req) {
        User user = findByEmail(req.getEmail());
        VerificationToken token = createVerificationToken(user);

        String subject = "Xác minh tài khoản của bạn";
        String verifyURL = "%s/reset-password?token=%s&userId=%s".formatted(frontendConfig.getUri(), token.getToken(),
                user.getId());

        String content = """
                    Xin chào %s,

                    Cảm ơn bạn đã đăng ký!
                    Hãy nhấp vào liên kết bên dưới để tài thay mật khẩu của bạn:
                    %s

                    Liên kết này sẽ hết hạn sau 24 giờ.

                    Trân trọng,
                    Hệ thống của chúng tôi.
                """.formatted(user.getFullName(), verifyURL);
        emailService.sendCustomMessage(user, content, subject);
    }

    @Override
    public User resetPassword(Long userId, String newPassword) {
        User user = findById(userId);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
