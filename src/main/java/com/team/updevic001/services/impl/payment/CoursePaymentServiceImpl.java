package com.team.updevic001.services.impl.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.team.updevic001.dao.entities.course.Course;
import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.entities.payment.UserCourseFee;
import com.team.updevic001.dao.repositories.AdminBalanceRepository;
import com.team.updevic001.dao.repositories.UserCourseFeeRepository;
import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.exceptions.AlreadyExistsException;
import com.team.updevic001.model.dtos.request.PaymentRequest;
import com.team.updevic001.model.dtos.response.payment.StripeResponse;
import com.team.updevic001.model.enums.TransactionType;
import com.team.updevic001.services.impl.course.CourseServiceImpl;
import com.team.updevic001.services.interfaces.PaymentService;
import com.team.updevic001.services.interfaces.StudentService;
import com.team.updevic001.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.team.updevic001.exceptions.ExceptionConstants.ALREADY_EXISTS_EXCEPTION;
import static com.team.updevic001.model.enums.TransactionType.INCOME;

@Service
@RequiredArgsConstructor
public class CoursePaymentServiceImpl implements PaymentService {

    private final AuthHelper authHelper;
    private final CourseServiceImpl courseServiceImpl;
    private final UserCourseFeeRepository userCourseFeeRepository;
    private final StudentService studentServiceImpl;
    private final TeachersPaymentTransactionService paymentsOfTeacherService;
    private final UserRepository userRepository;
    private final AdminBalanceRepository adminBalanceRepository;
    private final AdminBalanceService adminBalanceService;
    private final AdminTransactionService adminTransactionService;

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${frontend.url2}")
    private String frontEndUrl2;


    @Override
    public StripeResponse checkoutProducts(PaymentRequest request) {
        Stripe.apiKey = secretKey;
        User authenticatedUser = authHelper.getAuthenticatedUser();
        Course course = courseServiceImpl.findCourseById(request.getCourseId());

        boolean exists = userCourseFeeRepository.existsUserCourseFeeByCourseAndUser(course, authenticatedUser);
        if (exists) {
            throw new AlreadyExistsException(ALREADY_EXISTS_EXCEPTION.getCode(), "The user has already purchased the course.");
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
                .setSuccessUrl(frontEndUrl2 + "/success/" + request.getCourseId())
                .setCancelUrl(frontEndUrl2 + "/cancel/" + request.getCourseId())
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
    @Transactional
    public void paymentSuccess(String courseId) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        Course course = courseServiceImpl.findCourseById(courseId);
        UserCourseFee userCourseFee = UserCourseFee.builder()
                .user(authenticatedUser)
                .course(course)
                .payment(true)
                .build();
        userCourseFeeRepository.save(userCourseFee);

        studentServiceImpl.enrollInCourse(courseId, authenticatedUser);
        adminTransactionService.balanceIncrease(
               BigDecimal.valueOf(course.getPrice()),
                "The student (" + authenticatedUser.getEmail() + ") paid for the course (" + courseId + ")");
        paymentsOfTeacherService.createTeacherPaymentTransaction(
                userRepository.getTeacherMainInfoById(course.getTeacher()),
                courseId,
                course.getPriceWithoutInterest());
    }
}
