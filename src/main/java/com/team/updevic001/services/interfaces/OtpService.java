package com.team.updevic001.services.interfaces;


import com.team.updevic001.dao.entities.User;
import com.team.updevic001.model.dtos.request.security.OtpRequest;

public interface OtpService {
    void sendOtp(User user);

    void verifyOtp(OtpRequest otpRequest);
}
