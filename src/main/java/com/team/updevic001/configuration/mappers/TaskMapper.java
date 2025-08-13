package com.team.updevic001.configuration.mappers;

import com.team.updevic001.dao.entities.Task;
import com.team.updevic001.model.dtos.response.task.ResponseTaskDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Data
public class TaskMapper {

    public ResponseTaskDto toDto(Task task) {
        return new ResponseTaskDto(
                task.getQuestions(),
                task.getOptions()
        );
    }
}
