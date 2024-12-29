package manager;

import models.Epic;
import models.Subtask;
import models.Task;
import models.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Managers;

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
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskType.TASK);
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
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskType.TASK);
        Task taskWithGeneratedId = taskManager.createNewTask(task);

        assertNotNull(taskWithGeneratedId);

        Task foundTaskById = taskManager.getById(taskWithGeneratedId.getId());
        assertNotNull(foundTaskById);
    }

    @Test
    void taskManager_can_add_subtask() {
        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description");
        Epic epic = new Epic("test_epic", "test_desc");
        taskManager.createNewTask(epic);
        subtask.setEpic(epic);
        Task taskWithGeneratedId = taskManager.createNewTask(subtask);

        assertNotNull(taskWithGeneratedId);

        Task foundTaskById = taskManager.getById(taskWithGeneratedId.getId());
        assertNotNull(foundTaskById);
    }

    @Test
    void taskManager_can_add_epic() {
        Epic epic = new Epic("test_epic", "test_desc");

        Task taskWithGeneratedId = taskManager.createNewTask(epic);

        assertNotNull(taskWithGeneratedId);

        Task foundTaskById = taskManager.getById(taskWithGeneratedId.getId());
        assertNotNull(foundTaskById);
    }

    @Test
    void generatedId_do_not_conflict_with_concrete_id() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskType.TASK);
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
        Task task = new Task(name, desc, TaskType.TASK);

        Task taskWithGeneratedId = taskManager.createNewTask(task);

        assertNotNull(taskWithGeneratedId);

        Task foundTaskById = taskManager.getById(taskWithGeneratedId.getId());

        assertEquals(name, foundTaskById.getName());
        assertEquals(desc, foundTaskById.getDescription());
    }
}
