package com.team.updevic001.configuration.config.ai;

import com.team.updevic001.dao.entities.StudentTask;
import com.team.updevic001.dao.entities.Task;
import com.team.updevic001.dao.entities.TestResult;
import com.team.updevic001.dao.repositories.StudentTaskRepository;
import com.team.updevic001.dao.repositories.TestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiUtilForTask {
    private final AiGradingService aiGradingService;
    private final TestResultRepository testResultRepository;
    private final StudentTaskRepository studentTaskRepository;

    @Async
    public void saveTaskRelations(Task task, String answer, StudentTask studentTask, TestResult testResult, long totalTasks, long completedTasks) {
        AiGradeResult aiResult = aiGradingService.check(
                task.getQuestions(),
                task.getCorrectAnswer(),
                answer
        );
        double taskScore = aiResult.getScore(); // <-- AI-dan gələn bal

        studentTask.setFeedback(aiResult.getFeedback());
        studentTask.setCorrect(aiResult.getCorrect());
        studentTask.setScore(taskScore);
        studentTaskRepository.save(studentTask);

        double scorePercentage = 0;
        if (totalTasks > 0) {
            scorePercentage = ((double) completedTasks / totalTasks) * 100;
        }
        testResult.setScore(scorePercentage);
        testResultRepository.save(testResult);

    }
}
