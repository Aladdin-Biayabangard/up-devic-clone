package com.team.updevic001.model.dtos.response.admin_dasboard;

import lombok.Data;

@Data
public class DashboardResponse {
    Long totalUsers;
    Long activeUsers;
    Long pendingUsers;
    Long pendingApplicationsForTeaching;
}
