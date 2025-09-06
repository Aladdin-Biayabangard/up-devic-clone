package com.team.updevic001.model.dtos.response.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTaskDto {

    private Long id;

    private String questions;

    private boolean submitted =false;

    private List<String> options;

    private String correctAnswer;

    private String studentAnswer;

    private double score;
}
