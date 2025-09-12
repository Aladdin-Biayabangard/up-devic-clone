package com.team.updevic001.model.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceInfoDto {
    private String device;
    private String os;
    private String browser;
    private String location;      // Yeni
    private LocalDateTime lastActive; // Yeni
    private boolean currentSession;   // Yeni
}