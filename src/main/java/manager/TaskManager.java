package manager;

import history.HistoryManager;
import models.Epic;
import models.Subtask;
import models.Task;

import java.util.List;
import java.util.Set;

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

    void validateTimeCrossing(Task newtask);

    Set<Task> getPrioritizedTasks();

    void removeTask(long taskId);

    void removeSubtask(long subtaskId);

    void removeEpic(long epicId);

    void removeTasks();

    void removeSubtasks();

    void removeEpics();

    void removeAllTasks();
}
