package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webtech.online.course.models.User;
import webtech.online.course.models.UserSession;
import webtech.online.course.repositories.UserSessionReposirory;
import webtech.online.course.repositories.VerificationTokenRepository;
import webtech.online.course.services.UserSessionService;

import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionReposirory userSessionReposirory;
    @Override
    public UserSession save(UserSession userSession) {
        return userSessionReposirory.save(userSession);
    }

    @Override
    @Transactional
    public void enforceMaxSession(User user, int maxSessions) {
        List<UserSession> activeSessions = userSessionReposirory.findActiveSessionByUserId(user.getId());

        if (activeSessions.size() >= maxSessions) {
            activeSessions.sort(Comparator.comparing(UserSession::getCreatedAt));
            int sessionsToRemove = activeSessions.size() - maxSessions + 1;
            for (int i = 0; i < sessionsToRemove; i++) {
                UserSession s = activeSessions.get(i);
                s.setRevoked(true);
                userSessionReposirory.save(s);
            }
        }
    }
}
