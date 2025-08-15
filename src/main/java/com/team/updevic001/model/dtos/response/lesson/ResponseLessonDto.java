package com.team.updevic001.model.dtos.response.lesson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseLessonDto {

    private Long lessonId;

    private String photoUrl;

    private String title;

    private String description;

    private String videoUrl;

    private String duration;

//    private List<ResponseCommentDto> comments;

}
