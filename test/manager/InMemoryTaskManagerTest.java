package manager;

import exception.ManagerSaveException;
import models.Epic;
import models.Subtask;
import models.Task;
import models.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private final TaskManager taskManager = Managers.getInMemoryTaskManager();

    @BeforeEach
    void setUp() {
        taskManager.resetCounter();
    }

    @Test
    void createNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskType.TASK, LocalDateTime.now(), Duration.ofMinutes(400));
        final long taskId = taskManager.createNewTask(task).getId();

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void taskManager_can_add_task() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskType.TASK, LocalDateTime.now(), Duration.ofMinutes(400));
        Task taskWithGeneratedId = taskManager.createNewTask(task);

        assertNotNull(taskWithGeneratedId);

        Task foundTaskById = taskManager.getById(taskWithGeneratedId.getId());
        assertNotNull(foundTaskById);
    }

    @Test
    void taskManager_can_add_subtask() {
        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description", LocalDateTime.now(), Duration.ofMinutes(200));
        Epic epic = new Epic("test_epic", "test_desc", LocalDateTime.now(), Duration.ZERO);
        taskManager.createNewTask(epic);
        subtask.setEpic(epic);
        Task taskWithGeneratedId = taskManager.createNewTask(subtask);

        assertNotNull(taskWithGeneratedId);

        Task foundTaskById = taskManager.getById(taskWithGeneratedId.getId());
        assertNotNull(foundTaskById);
    }

    @Test
    void taskManager_can_add_epic() {
        Epic epic = new Epic("test_epic", "test_desc", LocalDateTime.now(), Duration.ZERO);

        Task taskWithGeneratedId = taskManager.createNewTask(epic);

        assertNotNull(taskWithGeneratedId);

        Task foundTaskById = taskManager.getById(taskWithGeneratedId.getId());
        assertNotNull(foundTaskById);
    }

    @Test
    void generatedId_do_not_conflict_with_concrete_id() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskType.TASK, LocalDateTime.now(), Duration.ofMinutes(400));
        long concreteId = 10L;
        task.setId(concreteId);

        Task taskWithGeneratedId = taskManager.createNewTask(task);

        assertNotNull(taskWithGeneratedId);

        assertNotEquals(concreteId, taskWithGeneratedId.getId());
    }

    @Test
    void should_not_change_field_after_adding_into_manager() {
        String name = "Test addNewTask";
        String desc = "Test addNewTask description";
        Task task = new Task(name, desc, TaskType.TASK, LocalDateTime.now(), Duration.ofMinutes(400));

        Task taskWithGeneratedId = taskManager.createNewTask(task);

        assertNotNull(taskWithGeneratedId);

        Task foundTaskById = taskManager.getById(taskWithGeneratedId.getId());

        assertEquals(name, foundTaskById.getName());
        assertEquals(desc, foundTaskById.getDescription());
    }

    @Test
    void should_throw_managerException() {
        Task task1 = new Task("task1", "task1_desc1", TaskType.TASK, LocalDateTime.now(), Duration.ofMinutes(400));
        taskManager.createNewTask(task1);
        Task task2 = new Task("task2", "task2_desc2", TaskType.TASK, LocalDateTime.now(), Duration.ofMinutes(300));

        var exception = assertThrows(ManagerSaveException.class, () -> taskManager.createNewTask(task2));

        assertEquals("New Task with ID = %d is crossing existing task with ID = %d".formatted(2L, 1L),
                exception.getMessage());
    }

    @Test
    void should_not_throw_managerException() {
        Task task1 = new Task("task1", "task1_desc1", TaskType.TASK, LocalDateTime.now(), Duration.ofMinutes(400));
        taskManager.createNewTask(task1);
        Task task2 = new Task("task2", "task2_desc2", TaskType.TASK, LocalDateTime.now().plusDays(5), Duration.ofMinutes(300));

        assertDoesNotThrow(() -> taskManager.createNewTask(task2));
    }
}
