package com.team.updevic001.services.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.Teacher;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserCourseFee;
import com.team.updevic001.dao.repositories.TeacherRepository;
import com.team.updevic001.dao.repositories.UserCourseFeeRepository;
import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.exceptions.ResourceAlreadyExistException;
import com.team.updevic001.mail.EmailServiceImpl;
import com.team.updevic001.mail.EmailTemplate;
import com.team.updevic001.model.dtos.request.PaymentRequest;
import com.team.updevic001.model.dtos.response.payment.StripeResponse;
import com.team.updevic001.model.enums.Role;
import com.team.updevic001.model.projection.UserView;
import com.team.updevic001.services.interfaces.PaymentService;
import com.team.updevic001.services.interfaces.StudentService;
import com.team.updevic001.services.interfaces.TeacherService;
import com.team.updevic001.utility.AuthHelper;
import com.team.updevic001.utility.Export;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final AuthHelper authHelper;
    private final CourseServiceImpl courseServiceImpl;
    private final UserCourseFeeRepository userCourseFeeRepository;
    private final StudentService studentServiceImpl;
    private final TeacherService teacherServiceImpl;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailServiceImpl;
    private final Export export;

    @Value("${stripe.secret.key}")
    private String secretKey;


    @Override
    public StripeResponse checkoutProducts(PaymentRequest request) {
        Stripe.apiKey = secretKey;
        User authenticatedUser = authHelper.getAuthenticatedUser();
        Course course = courseServiceImpl.findCourseById(request.getCourseId());

        boolean exists = userCourseFeeRepository.existsUserCourseFeeByCourseAndUser(course, authenticatedUser);
        if (exists) {
            throw new ResourceAlreadyExistException("The user has already purchased the course.");
        }

        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(request.getCourseId()).build();

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("USD")
                .setUnitAmount((long) (request.getAmount() * 100))
                .setProductData(productData)
                .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(priceData)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/api/payment/success?courseId=" + request.getCourseId())
                .setCancelUrl("http://localhost:8080/api/payment")
                .addLineItem(lineItem)
                .build();

        Session session;

        try {
            session = Session.create(params);

        } catch (StripeException e) {
            throw new IllegalArgumentException(e);
        }
        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment session created!")
                .courseId(request.getCourseId())
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

    @Override
    public void paymentStatus(String courseId) {
        User authenticatedUser = authHelper.getAuthenticatedUser();

        Course course = courseServiceImpl.findCourseById(courseId);

        UserCourseFee userCourseFee = UserCourseFee.builder()
                .user(authenticatedUser)
                .course(course)
                .payment(true)
                .build();
        userCourseFeeRepository.save(userCourseFee);
        studentServiceImpl.enrollInCourse(courseId, authenticatedUser);
        Teacher headTeacher = course.getHeadTeacher();
        BigDecimal balance = headTeacher.getBalance();
        if (balance != null) {
            balance = balance.add(BigDecimal.valueOf(course.getPrice()));
        } else {
            balance = BigDecimal.valueOf(course.getPrice());
        }
        headTeacher.setBalance(balance);
        teacherRepository.save(headTeacher);
    }


    @Override
    public BigDecimal teacherBalance() {
        Teacher authenticatedTeacher = teacherServiceImpl.getAuthenticatedTeacher();
        return authenticatedTeacher.getBalance() == null ? BigDecimal.ZERO : authenticatedTeacher.getBalance();
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void resetTeacherBalance() {
        LocalDate today = LocalDate.now();

        if (today.getDayOfMonth() == 1) {
            List<Teacher> teacherByBalanceGreaterThan = teacherRepository.findTeacherByBalanceGreaterThanEqual(BigDecimal.ZERO);

            List<UserView> admins = userRepository.findUsersByRole(Role.ADMIN);
            admins.forEach(user -> {
                try {
                    File file = export.exportToExcel(teacherByBalanceGreaterThan);
                    emailServiceImpl.sendFileEmail(user.getEmail(), EmailTemplate.BALANCE_RESET_INFO, new HashMap<>(), file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            teacherByBalanceGreaterThan.forEach(teacher -> teacher.setBalance(BigDecimal.ZERO));
            teacherRepository.saveAll(teacherByBalanceGreaterThan);

        }
    }


}
