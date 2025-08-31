package com.team.updevic001.configuration.config.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResultDto {
    private boolean correct;
    private double score;
    private String feedback;
    private String correctAnswer;
}
