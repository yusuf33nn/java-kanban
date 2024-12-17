package manager;

import history.HistoryManager;
import models.Epic;
import models.Subtask;
import models.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    Task createNewTask(Task task);

    Task getById(long taskId);

    List<Subtask> getEpicSubtasks(long epicId);

    void updateTask(Task updatedTask);

    void removeById(long taskId);

    Task getTask(long taskId);

    Subtask getSubtask(long subtaskId);

    Epic getEpic(long epicId);

    HistoryManager getHistoryManager();

    void resetCounter();
}
