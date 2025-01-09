package models;

import manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static utils.Managers.getInMemoryTaskManager;

class EpicStatusTest {

    private final TaskManager taskManager = getInMemoryTaskManager();
    @Test
    void calculateEpicStatuses_when_all_subtasks_are_new() {
        Epic epic1 = new Epic("epic1", "epic1_desc1", LocalDateTime.now(), Duration.ZERO);
        taskManager.createNewTask(epic1);

        Subtask subtask1 = new Subtask("subtask1", "subtask1_desc1", LocalDateTime.now(), Duration.ofMinutes(200));
        subtask1.setEpic(epic1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2_desc2", LocalDateTime.now(), Duration.ofMinutes(300));
        subtask2.setEpic(epic1);
        Subtask subtask3 = new Subtask("subtask3", "subtask3_desc3", LocalDateTime.now(), Duration.ofMinutes(900));
        subtask3.setEpic(epic1);
    }
}