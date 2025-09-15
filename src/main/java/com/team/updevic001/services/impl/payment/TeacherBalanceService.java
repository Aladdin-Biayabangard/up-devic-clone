package com.team.updevic001.services.impl.payment;

import com.team.updevic001.dao.entities.payment.TeachersBalance;
import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.repositories.TeachersBalanceRepository;
import com.team.updevic001.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.team.updevic001.exceptions.ExceptionConstants.TEACHER_BALANCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TeacherBalanceService {

    private final TeachersBalanceRepository teachersBalanceRepository;


    public void createTeacherBalance(User teacher) {
        teachersBalanceRepository.save(TeachersBalance.builder()
                .teacher(teacher)
                .balance(BigDecimal.ZERO)
                .build());
    }

    public TeachersBalance fetchTeacherBalanceIfExists(Long teacherId) {
        return teachersBalanceRepository.findByTeacherId(teacherId).orElseThrow(() ->
                new NotFoundException(TEACHER_BALANCE_NOT_FOUND.getCode(),
                        TEACHER_BALANCE_NOT_FOUND.getMessage().formatted(teacherId)));
    }
}
