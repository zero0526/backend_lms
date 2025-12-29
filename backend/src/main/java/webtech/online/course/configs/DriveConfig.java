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
import com.google.api.services.drive.DriveScopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Configuration
@Slf4j
public class DriveConfig {

        @Value("${youtube.client.id}")
        private String clientId;

        @Value("${youtube.client.secret}")
        private String clientSecret;

        @Autowired
        private DbDataStoreFactory dbDataStoreFactory;

        private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
        private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);

        @Bean
        public Drive drive() throws GeneralSecurityException, IOException {
                final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                Credential credential = getCredentials(httpTransport);

                return new Drive.Builder(httpTransport, JSON_FACTORY, credential)
                                .setApplicationName("DriveUploadApp")
                                .build();
        }

        private Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
                GoogleClientSecrets clientSecrets = new GoogleClientSecrets()
                                .setInstalled(new GoogleClientSecrets.Details()
                                                .setClientId(clientId)
                                                .setClientSecret(clientSecret));

                GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                                .setDataStoreFactory(dbDataStoreFactory)
                                .setAccessType("offline")
                                .setApprovalPrompt("force")
                                .build();

                LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                                .setPort(8888)
                                .build();

                return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        }
}
