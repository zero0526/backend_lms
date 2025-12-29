package webtech.online.course.configs;

import io.livekit.server.WebhookReceiver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WebhookConfig {

    private final LiveKitConfig liveKitConfig;

    @Bean
    public WebhookReceiver webhookReceiver() {
        return new WebhookReceiver(
                liveKitConfig.getApiKey(),
                liveKitConfig.getApiSecret());
    }
}
