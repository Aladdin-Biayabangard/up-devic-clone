package com.team.updevic001.scheduler;

import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.model.dtos.notification.UserEmailInfo;
import com.team.updevic001.services.impl.common.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserReminder {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 12 * * *")
    @Async
    public void userReminder() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<UserEmailInfo> usersInactiveSince = userRepository.findUsersInactiveSince(oneMonthAgo);
        usersInactiveSince.forEach(notificationService::sendNotificationForReminder);
    }
}
