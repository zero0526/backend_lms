package webtech.online.course.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import webtech.online.course.models.VerificationToken;
import webtech.online.course.repositories.VerificationTokenRepository;
import webtech.online.course.services.VerificationTokenService;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;
    @Override
    public VerificationToken findByToken(String token) {
        return verificationTokenRepository.findByToken(token).orElseThrow(()->new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn"));
    }

    @Override
    public void delete(VerificationToken token) {
        verificationTokenRepository.delete(token);
    }
}
