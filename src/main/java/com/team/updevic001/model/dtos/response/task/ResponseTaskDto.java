package com.team.updevic001.model.dtos.response.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTaskDto {

    private String questions;

    private List<String> options;
}
