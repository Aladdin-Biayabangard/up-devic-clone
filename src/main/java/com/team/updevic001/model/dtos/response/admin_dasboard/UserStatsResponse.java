package com.team.updevic001.model.dtos.response.admin_dasboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserStatsResponse {
    Long totalUsers;
    Long activeUsers;
    Long pendingUsers;
}
