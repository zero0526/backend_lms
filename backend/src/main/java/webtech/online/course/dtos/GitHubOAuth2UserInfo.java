package webtech.online.course.dtos;

import java.util.Map;

public class GitHubOAuth2UserInfo extends OAuth2UserInfo {
    public GitHubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return ((Integer) attributes.get("id")).toString();
    }

    @Override
    public String getName() {
        String name = (String) attributes.get("name");
        if (name == null) {
            return (String) attributes.get("login");
        }
        return name;
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }

    @Override
    public String getUserProviderId() {
        Object id = attributes.get("id");
        return id != null ? String.valueOf(id) : null;
    }
}
