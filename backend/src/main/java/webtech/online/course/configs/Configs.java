package webtech.online.course.configs;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class Configs {
    @Value("${app.token.expire-seconds}")
    private Long expireSecondsInstance;
    private static Long expireSeconds;

    @PostConstruct
    public void init() {
        expireSeconds = expireSecondsInstance;
    }
    public static Long getExpireSeconds() {
        return expireSeconds;
    }
}
