package webtech.online.course.security;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization"
        );
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        return customizeRequest(defaultResolver.resolve(request), request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return customizeRequest(defaultResolver.resolve(request, clientRegistrationId), request);
    }

    private OAuth2AuthorizationRequest customizeRequest(OAuth2AuthorizationRequest originalRequest, HttpServletRequest request) {
        if (originalRequest == null) {
            return null;
        }

        String role = request.getParameter("role");
        Map<String, Object> additionalParameters = new HashMap<>(originalRequest.getAdditionalParameters());

        if (role != null && !role.isEmpty()) {
            additionalParameters.put("OAUTH_ROLE", role);

            HttpSession session = request.getSession();
            session.setAttribute("OAUTH_ROLE", role);

            System.out.println("[DEBUG] OAuth2 role saved in session: " + role);
        }

        return OAuth2AuthorizationRequest.from(originalRequest)
                .additionalParameters(additionalParameters)
                .build();
    }
}
