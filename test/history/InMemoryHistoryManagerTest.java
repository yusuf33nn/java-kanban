package history;

import enums.TaskStatus;
import manager.TaskManager;
import models.Task;
import models.TaskType;
import org.junit.jupiter.api.Test;
import utils.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private final TaskManager taskManager = Managers.getInMemoryTaskManager();

    @Test
    void should_save_task_status_in_history() {
        Task task1 = new Task("Test addNewTask", "Test addNewTask description", TaskType.TASK, LocalDateTime.now(), Duration.ofMinutes(400));
        taskManager.createNewTask(task1);

        Task retrievedTask1 = taskManager.getTask(task1.getId());
        assertNotNull(retrievedTask1);
        assertEquals(TaskStatus.NEW, task1.getStatus());

        task1.setStatus(TaskStatus.IN_PROGRESS);
        Task retrievedTask1Again = taskManager.getTask(task1.getId());
        assertNotNull(retrievedTask1Again);

        List<Task> history = taskManager.getHistoryManager().getHistory();
        assertEquals(1, history.size());
        assertEquals(TaskStatus.IN_PROGRESS, history.getLast().getStatus());
    }
}
