package com.team.updevic001.configuration.config.ai;

import lombok.Data;

@Data
public class AiGradeResult {
    private Boolean correct;
    private String feedback;
    private String correctAnswer;
    private double score; // 0–100 arası qiymət
}
