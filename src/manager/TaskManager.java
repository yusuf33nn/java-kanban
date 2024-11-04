package manager;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaskManager {
    private static long TASK_ID_COUNTER = 0;

    private final Map<Long, Task> taskMap = new HashMap<>();
    private final Map<Long, Task> subtaskMap = new HashMap<>();
    private final Map<Long, Task> epicMap = new HashMap<>();

    public List<Task> getAllTasks() {
        return taskMap.values().stream().toList();
    }

    public List<Task> getAllSubtasks() {
        return subtaskMap.values().stream().toList();
    }

    public List<Task> getAllEpics() {
        return epicMap.values().stream().toList();
    }

    public Task createNewTask(Task task) {
        long taskId = ++TASK_ID_COUNTER;
        task.setId(taskId);
        if (task instanceof Subtask) {
            subtaskMap.put(taskId, task);
            return task;
        } else if (task instanceof Epic) {
            epicMap.put(taskId, task);
            return task;
        } else {
            taskMap.put(taskId, task);
            return task;
        }
    }

    public Task getById(long taskId) {
        Task task = taskMap.getOrDefault(taskId, null);

        if (task == null) {
            task = subtaskMap.getOrDefault(taskId, null);
        }

        if (task == null) {
            task = Optional.ofNullable(epicMap.getOrDefault(taskId, null))
                    .orElseThrow(() -> new RuntimeException("Task with taskId = %d doesn't not exist".formatted(taskId)));
        }
        return task;
    }


    public void updateTask(Task updatedTask) {
        long taskId = updatedTask.getId();
        if (taskMap.containsKey(taskId)) {
            taskMap.put(taskId, updatedTask);
        } else if (subtaskMap.containsKey(taskId)) {
            subtaskMap.put(taskId, updatedTask);
        } else if (epicMap.containsKey(taskId)){
            epicMap.put(taskId, updatedTask);
        } else {
            throw new RuntimeException("Task with id = %d doesn't not exist".formatted(taskId));
        }
    }

    public void removeById(long taskId) {
        if (taskMap.containsKey(taskId)) {
            taskMap.remove(taskId);
        } else if (subtaskMap.containsKey(taskId)) {
            subtaskMap.remove(taskId);
        } else if (epicMap.containsKey(taskId)){
            epicMap.remove(taskId);
        } else {
            throw new RuntimeException("Task with id = %d doesn't not exist".formatted(taskId));
        }
    }

    public void removeAllTasks() {
        taskMap.clear();
        subtaskMap.clear();
        epicMap.clear();
    }
}
