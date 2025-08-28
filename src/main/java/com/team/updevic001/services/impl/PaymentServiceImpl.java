package com.team.updevic001.services.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserCourseFee;
import com.team.updevic001.dao.entities.UserProfile;
import com.team.updevic001.dao.repositories.UserCourseFeeRepository;
import com.team.updevic001.dao.repositories.UserProfileRepository;
import com.team.updevic001.exceptions.AlreadyExistsException;
import com.team.updevic001.model.dtos.request.PaymentRequest;
import com.team.updevic001.model.dtos.response.payment.StripeResponse;
import com.team.updevic001.services.interfaces.PaymentService;
import com.team.updevic001.services.interfaces.StudentService;
import com.team.updevic001.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;


import static com.team.updevic001.model.enums.ExceptionConstants.ALREADY_EXISTS_EXCEPTION;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final AuthHelper authHelper;
    private final CourseServiceImpl courseServiceImpl;
    private final UserCourseFeeRepository userCourseFeeRepository;
    private final StudentService studentServiceImpl;
    private final UserProfileRepository userProfileRepository;

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
                .setSuccessUrl(frontEndUrl2 + "/v1/course/" + request.getCourseId())
                .setCancelUrl(frontEndUrl2 + "/v1/course/" + request.getCourseId())
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
        UserProfile userProfile = userProfileRepository.findByUser(authenticatedUser);
        BigDecimal balance = userProfile.getBalance();
        if (balance != null) {
            balance = balance.add(BigDecimal.valueOf(course.getPrice()));
        } else {
            balance = BigDecimal.valueOf(course.getPrice());
        }
        userProfile.setBalance(balance);
        userProfileRepository.save(userProfile);
    }


    @Override
    public BigDecimal teacherBalance() {
        UserProfile userProfile = userProfileRepository.findByUser(authHelper.getAuthenticatedUser());
        return userProfile.getBalance() == null ? BigDecimal.ZERO : userProfile.getBalance();
    }

//    @Scheduled(cron = "0 0 8 * * *")
//    public void resetTeacherBalance() {
//        LocalDate today = LocalDate.now();
//
//        if (today.getDayOfMonth() == 1) {
//            List<Teacher> teacherByBalanceGreaterThan = teacherRepository.findTeacherByBalanceGreaterThanEqual(BigDecimal.ZERO);
//
//            List<UserView> admins = userRepository.findUsersByRole(Role.ADMIN);
//            admins.forEach(user -> {
//                try {
//                    File file = export.exportToExcel(teacherByBalanceGreaterThan);
//                    emailServiceImpl.sendFileEmail(user.getEmail(), EmailTemplate.BALANCE_RESET_INFO, new HashMap<>(), file);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            teacherByBalanceGreaterThan.forEach(teacher -> teacher.setBalance(BigDecimal.ZERO));
//            teacherRepository.saveAll(teacherByBalanceGreaterThan);
//
//        }
//    }


}
