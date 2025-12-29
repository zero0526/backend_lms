package webtech.online.course.configs;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import webtech.online.course.configs.DbDataStoreFactory;

@Configuration
@Slf4j
public class YouTubeConfig {

        @Value("${youtube.client.id}")
        private String clientId;

        @Value("${youtube.client.secret}")
        private String clientSecret;

        @Autowired
        private DbDataStoreFactory dbDataStoreFactory;

        private static final String APPLICATION_NAME = "VideoUploadApp";
        private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
        private static final List<String> SCOPES = Arrays.asList(
                        YouTubeScopes.YOUTUBE,
                        YouTubeScopes.YOUTUBE_UPLOAD,
                        YouTubeScopes.YOUTUBE_READONLY);

        @Bean
        public YouTube youTube() throws GeneralSecurityException, IOException {
                final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                Credential credential = getCredentials(httpTransport);

                return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                                .setApplicationName(APPLICATION_NAME)
                                .build();
        }

        private Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
                GoogleClientSecrets clientSecrets = new GoogleClientSecrets()
                                .setInstalled(new GoogleClientSecrets.Details()
                                                .setClientId(clientId)
                                                .setClientSecret(clientSecret));

                log.info("Sử dụng Database để lưu trữ credentials");

                GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                                .setDataStoreFactory(dbDataStoreFactory)
                                .setAccessType("offline")
                                .setApprovalPrompt("force") // Buộc hiển thị consent screen để lấy refresh token
                                .build();

                LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                                .setPort(8888)
                                .build();

                log.info("Bắt đầu OAuth flow cho YouTube. Vui lòng mở browser...");
                // Use a fixed user ID so we can reuse the same token
                Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("youtube_user");
                log.warn("YouTube OAuth flow hoàn tất thành công!");
                log.warn("Access Token: {}", credential.getAccessToken());
                log.warn("Refresh Token: {}", credential.getRefreshToken());
                log.warn("Token Expiration: {}", credential.getExpirationTimeMilliseconds());

                return credential;
        }
}