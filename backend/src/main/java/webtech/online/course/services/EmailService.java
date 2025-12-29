package webtech.online.course.services;

import webtech.online.course.models.User;
import webtech.online.course.models.VerificationToken;

public interface EmailService {
    public void sendSimpleMessage(User user, VerificationToken token);
    public void sendCustomMessage(User user, String message, String subject);
}
