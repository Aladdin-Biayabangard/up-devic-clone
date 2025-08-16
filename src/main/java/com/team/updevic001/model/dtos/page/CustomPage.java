package com.team.updevic001.model.dtos.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPage<T> {

    private List<T> content;
    private int page;
    private int size;
}
