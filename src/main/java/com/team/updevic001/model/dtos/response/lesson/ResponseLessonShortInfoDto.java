package com.team.updevic001.model.dtos.response.lesson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseLessonShortInfoDto {

    private String lessonId;

    private String photoUrl;

    private String title;

    private String description;

}
