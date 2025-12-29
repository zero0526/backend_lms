-- liquibase formatted sql

-- changeset antigravity:002-insert-sample-data
-- validCheckSum: 9:c55b84ede4731b92873cd3cfd0d1df4a
INSERT INTO oauth_providers
(name, client_id, client_secret_key, redirect_uri, auth_url, token_url, userinfo_url, scopes)
VALUES
(
    'google',
    '${oauth_google_client_id}',
    '${oauth_google_client_secret}',
    'http://localhost:8081/login/oauth2/code/google',
    'https://accounts.google.com/o/oauth2/v2/auth',
    'https://oauth2.googleapis.com/token',
    'https://openidconnect.googleapis.com/v1/userinfo',
    ARRAY['email','profile']
),
(
    'github',
    '${oauth_github_client_id}',
    '${oauth_github_client_secret}',
    'http://localhost:8081/login/oauth2/code/github',
    'https://github.com/login/oauth/authorize',
    'https://github.com/login/oauth/access_token',
    'https://api.github.com/user',
    ARRAY['read:user','user:email']
);

INSERT INTO roles(name) VALUES
('ROLE_TEACHER'),
('ROLE_STUDENT');