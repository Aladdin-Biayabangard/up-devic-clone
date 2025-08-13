package com.team.updevic001.model.dtos.response.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCommentDto {

    private Long commentId;

    private String firstName;

    private String content;

    private LocalDateTime updatedAt;

}
