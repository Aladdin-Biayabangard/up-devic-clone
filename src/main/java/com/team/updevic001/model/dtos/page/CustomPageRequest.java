package com.team.updevic001.model.dtos.page;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class CustomPageRequest {
    private int page = 0;
    private int size = 10;
}
