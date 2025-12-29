package webtech.online.course.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Service;
import webtech.online.course.models.OAuthProvider;
import webtech.online.course.repositories.OAuthProviderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamicClientRegistrationService {
    private final OAuthProviderRepository oauthProviderRepository;

    public ClientRegistration buildClientRegistration(OAuthProvider provider) {
        String userNameAttr = switch(provider.getName()) {
            case "google" -> "sub";
            case "github" -> "id";
            default -> "sub";
        };
        return ClientRegistration.withRegistrationId(provider.getName())
                .clientId(provider.getClientId())
                .clientSecret(provider.getClientSecretKey())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(provider.getRedirectUri())
                .scope(provider.getScopes().toArray(new String[0]))
                .authorizationUri(provider.getAuthUrl())
                .tokenUri(provider.getTokenUrl())
                .userInfoUri(provider.getUserinfoUrl())
                .userNameAttributeName(userNameAttr)
                .clientName(provider.getName())
                .build();
    }

    public List<ClientRegistration> getAllRegistrations() {
        return oauthProviderRepository.findAll()
                .stream()
                .map(this::buildClientRegistration)
                .collect(Collectors.toList());
    }
}
