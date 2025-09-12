package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.LoginHistory;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.repositories.LoginHistoryRepository;
import com.team.updevic001.model.dtos.response.DeviceInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;
    private final GeoService geoService;

    @Async
    public void saveLoginHistory(User user, HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();

        // Location-i al
        String location = geoService.getLocation(ip);

        // Köhnə bütün session-ları false et
        loginHistoryRepository.updateCurrentSessionFalse(user.getId());

        DeviceInfoDto deviceInfoDto = parse(userAgent);
        deviceInfoDto.setLocation(location);
        deviceInfoDto.setLastActive(LocalDateTime.now());
        deviceInfoDto.setCurrentSession(true);

        // Yeni login history
        if (!checkDeviceRegistered(user.getId(), deviceInfoDto)) {
            LoginHistory loginHistory = LoginHistory.builder()
                    .user(user)
                    .device(deviceInfoDto.getDevice())
                    .os(deviceInfoDto.getOs())
                    .browser(deviceInfoDto.getBrowser())
                    .location(deviceInfoDto.getLocation())
                    .loginTime(deviceInfoDto.getLastActive())
                    .currentSession(deviceInfoDto.isCurrentSession())
                    .build();
            loginHistoryRepository.save(loginHistory);
        }
    }

    public List<DeviceInfoDto> getDevicesInfo(Long userId) {
        List<LoginHistory> histories = loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(userId);

        return histories.stream().map(h -> {
            DeviceInfoDto dto = new DeviceInfoDto();
            dto.setDevice(h.getDevice());
            dto.setOs(h.getOs());
            dto.setBrowser(h.getBrowser());
            dto.setLocation(h.getLocation());
            dto.setLastActive(h.getLoginTime());
            dto.setCurrentSession(h.isCurrentSession());
            return dto;
        }).collect(Collectors.toList());
    }

    // 🔹 Mövcud cihaz əvvəllər qeyd olunubmu?
    public boolean checkDeviceRegistered(Long userId, DeviceInfoDto deviceInfoDto) {
        boolean exists = loginHistoryRepository.existsByUserIdAndBrowserAndDeviceAndOs(
                userId,
                deviceInfoDto.getBrowser(),
                deviceInfoDto.getDevice(),
                deviceInfoDto.getOs()
        );
        log.debug("Device registration check for user ID {}: {}", userId, exists);
        return exists;
    }

    // 🔹 User-Agent parse
    public DeviceInfoDto parse(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            log.warn("User-Agent header is null or empty.");
            return new DeviceInfoDto("Unknown Device", "Unknown OS", "Unknown Browser", "Unknown", LocalDateTime.now(), false);
        }

        UserAgentAnalyzer uaa = UserAgentAnalyzer
                .newBuilder()
                .hideMatcherLoadStats()
                .withCache(10000)
                .build();

        UserAgent agent = uaa.parse(userAgent);

        String device = agent.getValue("DeviceName");
        String os = agent.getValue("OperatingSystemNameVersion");
        String browser = agent.getValue("AgentNameVersion");

        log.debug("Parsed device info: device={}, os={}, browser={}", device, os, browser);
        return new DeviceInfoDto(device, os, browser, null, LocalDateTime.now(), false);
    }
}
