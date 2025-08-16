package com.team.updevic001.model.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentRequest {
    private double amount; // məsələn 3500 (yəni 35.00 AZN)
    private String courseId;
    private String description;
}
