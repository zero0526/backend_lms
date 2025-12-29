package webtech.online.course.services;

import webtech.online.course.models.User;
import webtech.online.course.models.UserSession;

public interface UserSessionService {
    public UserSession save(UserSession userSession);
    public void enforceMaxSession(User user, int maxSessions);
}
