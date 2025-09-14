package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.TeacherPaymentTransaction;
import com.team.updevic001.dao.entities.TeachersBalance;
import com.team.updevic001.dao.repositories.TeacherPaymentTransactionRepository;
import com.team.updevic001.dao.repositories.TeachersBalanceRepository;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.response.payment.TeacherPaymentResponse;
import com.team.updevic001.model.dtos.response.teacher.TeacherMainInfo;
import com.team.updevic001.model.enums.PaymentStatus;
import com.team.updevic001.model.mappers.PaymentMapper;
import com.team.updevic001.services.interfaces.UserService;
import com.team.updevic001.specification.TeacherPaymentSpecification;
import com.team.updevic001.specification.criteria.TeacherPaymentCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.team.updevic001.exceptions.ExceptionConstants.TEACHER_PAYMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TeachersPaymentTransactionService {

    private final TeacherPaymentTransactionRepository teacherPaymentTransactionRepository;
    private final TeacherBalanceService teacherBalanceService;
    private final PaymentMapper paymentMapper;
    private final UserService userService;
    private final TeachersBalanceRepository teachersBalanceRepository;

    public CustomPage<TeacherPaymentResponse> getTeacherPayments(CustomPageRequest request,
                                                                 TeacherPaymentCriteria criteria) {
        int page = (request != null && request.getPage() >= 0) ? request.getPage() : 0;
        int size = (request != null && request.getSize() > 0) ? request.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size);

        Specification<TeacherPaymentTransaction> filter = null;
        if (criteria.getEmail() != null ||
            criteria.getStatus() != null ||
            criteria.getFromDate() != null ||
            criteria.getToDate() != null) {
            filter = TeacherPaymentSpecification.filter(criteria);
        }

        var teacherPayments = (filter == null)
                ? teacherPaymentTransactionRepository.findAll(pageable)
                : teacherPaymentTransactionRepository.findAll(filter, pageable);

        return new CustomPage<>(
                paymentMapper.toResponse(teacherPayments.getContent()),
                teacherPayments.getNumber(),
                teacherPayments.getSize()
        );
    }

    public void createTeacherPaymentTransaction(TeacherMainInfo teacher, String courseId, BigDecimal price) {
        var teacherPayment = TeacherPaymentTransaction.builder()
                .teacherId(teacher.getTeacherId())
                .teacherName(teacher.getTeacherName())
                .teacherEmail(teacher.getEmail())
                .courseId(courseId)
                .amount(price)
                .status(PaymentStatus.PENDING)
                .build();
        teacherPaymentTransactionRepository.save(teacherPayment);

        //Admine Notification gedecek ki odenis etmek lazimdir.
    }

    @Transactional
    public void toPayTheTeacher(Long id) {
        var teacherPaymentTransaction = fetchTeacherPayment(id);
        teacherPaymentTransaction.setStatus(PaymentStatus.PAID);
        teacherPaymentTransaction.setPaymentDateAndTime(LocalDateTime.now());
        //Burda yoxlamalar gedecek ki sistemin balansinda muellimin pulunu odemeye imkan var ya yox
        // eger varsa balansi artiririq muellimin
        var teacher = userService.fetchUserById(teacherPaymentTransaction.getTeacherId());
        TeachersBalance teachersBalance;
        try {
            teachersBalance = teacherBalanceService.fetchTeacherBalanceIfExists(teacher);
        } catch (NotFoundException e) {
            teachersBalance = TeachersBalance.builder()
                    .teacher(teacher)
                    .build();
        }
        teachersBalance.setBalance(teachersBalance.getBalance().add(teacherPaymentTransaction.getAmount()));
        teachersBalanceRepository.save(teachersBalance);
    }

    public void updateTeacherPaymentTransaction(Long paymentTeacherId, String description) {
        var teacherPayment = fetchTeacherPayment(paymentTeacherId);
        teacherPayment.setDescription(description);
        teacherPaymentTransactionRepository.save(teacherPayment);
    }

    public TeacherPaymentTransaction fetchTeacherPayment(Long id) {
        return teacherPaymentTransactionRepository.findById(id).orElseThrow(() -> new NotFoundException(TEACHER_PAYMENT_NOT_FOUND.getCode(),
                TEACHER_PAYMENT_NOT_FOUND.getMessage().formatted(id)));
    }
}
