package com.team.updevic001.model.dtos.response.task;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseSubmission {

    private double score;
    private String feedback;
    private String correctAnswer;
    private String studentAnswer;
    private boolean correct;
    private boolean submitted;
}
