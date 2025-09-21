package com.team.updevic001.model.dtos.request;

import com.team.updevic001.model.enums.CourseLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {

    private String title;

    private String description;

    private CourseLevel level;

    private double price;

    private Set<String> searchKeys;

    private Set<String> tags;
}
