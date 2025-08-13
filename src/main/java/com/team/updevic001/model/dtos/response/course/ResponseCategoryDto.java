package com.team.updevic001.model.dtos.response.course;

import com.team.updevic001.model.enums.CourseCategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCategoryDto {

    private CourseCategoryType category;

    private Integer courseCount;

}
