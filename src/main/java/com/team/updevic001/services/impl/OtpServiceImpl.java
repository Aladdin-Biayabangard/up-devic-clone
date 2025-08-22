package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.Otp;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.repositories.OtpRepository;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.mail.EmailServiceImpl;
import com.team.updevic001.mail.EmailTemplate;
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

import static com.team.updevic001.model.enums.ExceptionConstants.OTP_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpServiceImpl implements OtpService {
    OtpRepository otpRepository;
    EmailServiceImpl emailService;

    @Override
    @Transactional
    public void sendOtp(User user) {
        String code = generateOtp(user.getEmail());
        Map<String, String> placeholders = Map.of("userName", user.getFirstName(), "code", code);
        emailService.sendEmail(user.getEmail(), EmailTemplate.VERIFICATION, placeholders);
    }


    @Override
    public void verifyOtp(OtpRequest otpRequest) {
        Otp otp = otpRepository.findByCodeAndEmail(otpRequest.getOtpCode(), otpRequest.getEmail())
                .orElseThrow(() ->
                        new NotFoundException(
                                OTP_NOT_FOUND.getCode(), OTP_NOT_FOUND.getMessage()
                                .formatted(otpRequest.getOtpCode())));
        if (otp.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP_EXPIRED");
        }
    }

    private String generateOtp(String email) {
        SecureRandom random = new SecureRandom();
        Integer code = 100_000 + random.nextInt(900_000);
        Otp otp = Otp.builder()
                .email(email)
                .code(code)
                .expirationTime(LocalDateTime.now().plusMinutes(15))
                .build();
        otpRepository.save(otp);
        return String.valueOf(code);
    }
}
