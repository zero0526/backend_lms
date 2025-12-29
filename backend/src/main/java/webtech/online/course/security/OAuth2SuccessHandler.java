package webtech.online.course.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import webtech.online.course.configs.FrontendConfig;
import webtech.online.course.dtos.OAuth2UserInfo;
import webtech.online.course.dtos.OAuth2UserInfoFactory;
import webtech.online.course.models.User;
import webtech.online.course.services.impl.UserServiceImpl;
import webtech.online.course.models.UserSession;
import webtech.online.course.services.UserSessionService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserServiceImpl userService;
    private final UserSessionService userSessionService;
    private final FrontendConfig frontendConfig;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String roleName = (String) request.getSession().getAttribute("OAUTH_ROLE");
        String providerId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerId, oauthUser.getAttributes());

        if (roleName == null) roleName = "ROLE_STUDENT";
        else roleName= "ROLE_%s".formatted(roleName);
        UserDetails userDetails;
        User newUser;
        try {
            userDetails = userDetailsService.loadUserByUsername(userInfo.getEmail());
            newUser= userService.findByEmail(userInfo.getEmail());
        } catch (UsernameNotFoundException ex) {
            newUser= userService.firstOAuth(userInfo, roleName, providerId);

            userDetails = userDetailsService.loadUserByUsername(userInfo.getEmail());
        }
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        String ipAddress= request.getRemoteAddr();

        userSessionService.enforceMaxSession(newUser, 1);

        UserSession userSession = UserSession.builder()
                .user(newUser)
                .refreshToken(refreshToken)
                .ipAddress(ipAddress)
                .build();
        userSessionService.save(userSession);
        response.setContentType("application/json");

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(3600*24)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(31536000)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        String finalUrl = UriComponentsBuilder.fromUriString(frontendConfig.getUri() + "/home")
                .queryParam("id", newUser.getId())
                .queryParam("fullName", newUser.getFullName())
                .queryParam("email", newUser.getEmail())
                .queryParam("role", newUser.getRole().getName())
                .queryParam("pictureUrl", newUser.getPictureUrl())
                .build()
                .encode()
                .toUriString();
        response.sendRedirect(finalUrl);
    }
}
