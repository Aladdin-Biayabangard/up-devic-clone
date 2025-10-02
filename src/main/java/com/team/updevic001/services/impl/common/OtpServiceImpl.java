package com.team.updevic001.services.impl.common;

import com.team.updevic001.configuration.config.mailjet.MailjetEmailService;
import com.team.updevic001.dao.entities.auth.Otp;
import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.repositories.OtpRepository;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.dtos.request.security.OtpRequest;
import com.team.updevic001.services.interfaces.OtpService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static com.team.updevic001.exceptions.ExceptionConstants.OTP_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpServiceImpl implements OtpService {

    OtpRepository otpRepository;
    private final MailjetEmailService mailjetEmailService;


    @Override
    @Transactional
    public void sendOtp(User user) {
        LocalDateTime now = LocalDateTime.now();

        Optional<Otp> existingOtp = otpRepository.findFirstByEmailAndUsedFalseOrderByCreatedAtDesc(user.getEmail());
        if (existingOtp.isPresent()) {
            Otp otp = existingOtp.get();

            // Əgər OTP hələ də keçərlidirsə və limit aşılmayıbsa
            if (otp.getExpirationTime().isAfter(now) && otp.getRetryCount() < 5) {
                otp.setRetryCount(otp.getRetryCount() + 1);
                otp.setLastSentTime(now);
                otpRepository.save(otp);
                resendEmail(user.getEmail(), user.getFirstName(), otp.getCode());
                return;
            }

            // Əgər vaxt keçibsə → yenisini yaradırıq
            otp.setUsed(true);
            otpRepository.save(otp);
        }

        // Yeni OTP yaradılır
        String code = generateOtp(user.getEmail());
        Map<String, Object> placeholders = Map.of("userName", user.getFirstName(), "code", code);
        mailjetEmailService.sendEmail("Your OTP information",
                user.getEmail(),
                "verification.html",
                placeholders, null, null);
    }

    @Override
    @Transactional
    public void verifyOtp(OtpRequest otpRequest) {
        Otp otp = otpRepository.findByCodeAndEmailAndUsedFalse(otpRequest.getOtpCode(), otpRequest.getEmail())
                .orElseThrow(() -> new NotFoundException(
                        OTP_NOT_FOUND.getCode(),
                        OTP_NOT_FOUND.getMessage().formatted(otpRequest.getOtpCode())));

        if (otp.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP_EXPIRED");
        }

        otp.setUsed(true);
        otpRepository.save(otp);
    }

    public String generateOtp(String email) {
        SecureRandom random = new SecureRandom();
        int code = 100_000 + random.nextInt(900_000);
        Otp otp = Otp.builder()
                .email(email)
                .code(code)
                .expirationTime(LocalDateTime.now().plusMinutes(5))
                .retryCount(1)
                .lastSentTime(LocalDateTime.now())
                .used(false)
                .build();
        otpRepository.save(otp);
        return String.valueOf(code);
    }

    private void resendEmail(String email, String firstName, int code) {
        Map<String, Object> placeholders = Map.of("userName", firstName, "code", String.valueOf(code));
        mailjetEmailService.sendEmail("Resend",
                email,
                "verification.html",
                placeholders, null, null);
    }
}
