package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.TeachersBalance;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.repositories.TeachersBalanceRepository;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.team.updevic001.exceptions.ExceptionConstants.TEACHER_BALANCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TeacherBalanceService {

    private final TeachersBalanceRepository teachersBalanceRepository;
    private final AuthHelper authHelper;

    public void transferTeacherBalanceToCard(String card, BigDecimal amount) {
        // Transfer ucun logic sonradan yazilacaq

        var teacher = authHelper.getAuthenticatedUser();
        var teachersBalanceEntity = fetchTeacherBalanceIfExists(teacher);
        teachersBalanceEntity.setBalance(teachersBalanceEntity.getBalance().subtract(amount));
        teachersBalanceRepository.save(teachersBalanceEntity);
    }

    public TeachersBalance fetchTeacherBalanceIfExists(User teacher) {
        return teachersBalanceRepository.findByTeacher(teacher).orElseThrow(() ->
                new NotFoundException(TEACHER_BALANCE_NOT_FOUND.getCode(),
                        TEACHER_BALANCE_NOT_FOUND.getMessage().formatted(teacher.getId())));
    }
}
