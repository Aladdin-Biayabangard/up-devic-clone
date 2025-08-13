package com.team.updevic001.model.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private String questions;

    private List<String> options;

    private String correctAnswer;
}
