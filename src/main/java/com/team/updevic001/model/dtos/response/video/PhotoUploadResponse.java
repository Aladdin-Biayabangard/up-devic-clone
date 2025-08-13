package com.team.updevic001.model.dtos.response.video;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PhotoUploadResponse {
    private String key;
    private String url;
}
