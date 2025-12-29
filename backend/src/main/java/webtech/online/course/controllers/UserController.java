package webtech.online.course.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import webtech.online.course.configs.FrontendConfig;
import webtech.online.course.dtos.User.*;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.exceptions.WrapperResponse;
import webtech.online.course.models.User;
import webtech.online.course.models.UserSession;
import webtech.online.course.models.VerificationToken;
import webtech.online.course.security.JwtService;
import webtech.online.course.security.UserDetailsServiceImpl;
import webtech.online.course.services.UserProfileService;
import webtech.online.course.services.UserService;
import webtech.online.course.services.UserSessionService;
import webtech.online.course.services.VerificationTokenService;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserProfileService userProfileService;
    private final VerificationTokenService verificationTokenService;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserSessionService userSessionService;
    private  final FrontendConfig frontendConfig;

    @GetMapping("/info/{userId}")
    public ResponseEntity<WrapperResponse> getUserInformation(@PathVariable Long userId){
        try{
            UserInformationDTO userInformationDTO= userProfileService.getDetailsInformation(userId);
            return ResponseEntity.ok(new WrapperResponse(200, userInformationDTO));
        }catch (Exception ex){
            throw new BaseError(ex.getMessage());
        }
    }
    @PutMapping("/update/info/{userId}")
    public ResponseEntity<WrapperResponse> updateProfile(@PathVariable Long userId, @ModelAttribute UpdateUserRequest req){
        try{
            UserInformationDTO userInformationDTO= userProfileService.updateUser(userId, req);
            return ResponseEntity.ok(new WrapperResponse(200, userInformationDTO));
        }catch (Exception ex){
            throw new BaseError(ex.getMessage());
        }
    }
    @PostMapping("/update/password/{userId}")
    public ResponseEntity<WrapperResponse> updatePassWord(@PathVariable Long userId, @RequestBody UpdatePasswordRequest updatePasswordRequest){
        try{
            userService.updatePassword(userId, updatePasswordRequest);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), "YourPassword has been changed"));
        }catch (Exception ex){
            throw new BaseError(ex.getMessage());
        }
    }
    @PostMapping("/forgot/password")
    public ResponseEntity<WrapperResponse> forgotPassWord(@RequestBody ForgotPasswordRequest forgotPasswordRequest){
        try{
            userService.requestPasswordReset(forgotPasswordRequest);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), "pls go to your gmail to confirm request forget password"));
        }catch (Exception ex){
            throw new BaseError(ex.getMessage());
        }
    }
    @PostMapping("/forgot")
    public void forgotPassWord(@RequestParam String token,
                               @RequestParam Long userId,
                               @RequestBody ResetPasswordRequest resetPasswordRequest,
                               HttpServletRequest request, HttpServletResponse response){
        try{
            VerificationToken verificationToken = verificationTokenService.findByToken(token);

            if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                throw new BaseError(HttpStatus.GONE.value(), "Your validate token expired");
            }
            verificationTokenService.delete(verificationToken);
            User user= userService.resetPassword(userId, resetPasswordRequest.getNewPassword());
            UserDetails userDetails= userDetailsService.loadUserByUsername(user.getEmail());
            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            userSessionService.enforceMaxSession(user, 1);
            userSessionService.save(UserSession.builder()
                    .user(user)
                    .refreshToken(refreshToken)
                    .ipAddress(request.getRemoteAddr())
                    .build());

            String redirectUrl = UriComponentsBuilder.fromUriString(frontendConfig.getUri() + "/oauth2/redirect")
                    .queryParam("accessToken", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .queryParam("email", user.getEmail())
                    .queryParam("name", user.getFullName())
                    .queryParam("type", "RESET_PASSWORD") // Để FE biết dẫn vào trang đổi pass
                    .build().toUriString();

            response.sendRedirect(redirectUrl);
        }catch (Exception ex){
            throw new BaseError(ex.getMessage());
        }
    }
}
