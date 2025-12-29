package webtech.online.course.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "oauth_providers")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OAuthProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "client_secret_key")
    private String clientSecretKey;
    @Column(name = "redirect_uri")
    private String redirectUri;
    @Column(name = "auth_url")
    private String authUrl;
    @Column(name = "token_url")
    private String tokenUrl;
    @Column(name = "userinfo_url")
    private String userinfoUrl;
    @Column(name = "scopes", columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> scopes;
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
