package com.team.updevic001.model.dtos.response.lesson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseLessonShortInfoDto {

    private Long lessonId;

    private String photoUrl;

    private String title;

    private String description;

}
