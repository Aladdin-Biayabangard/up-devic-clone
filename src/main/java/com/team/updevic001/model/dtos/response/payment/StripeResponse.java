package com.team.updevic001.model.dtos.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StripeResponse {

    private String status;
    private String message;
    private String courseId;
    private String sessionId;
    private String sessionUrl;
}
