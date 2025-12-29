package webtech.online.course.services;

import webtech.online.course.models.VerificationToken;

public interface VerificationTokenService {
    public VerificationToken findByToken(String token);
    public void delete(VerificationToken token);
}
