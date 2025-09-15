package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.response.DeviceInfoDto;
import com.team.updevic001.services.impl.common.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/histories")
@RequiredArgsConstructor
public class LoginHistoryController {

    private final LoginHistoryService loginHistoryService;

    @GetMapping
    public List<DeviceInfoDto> getDevicesInfo(@RequestParam Long userId) {
        return loginHistoryService.getDevicesInfo(userId);
    }
}
