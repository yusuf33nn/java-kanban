package manager;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.*;

public class TaskManager {
    private static long TASK_ID_COUNTER = 0;

    private final Map<Long, Task> taskMap = new HashMap<>();
    private final Map<Long, Subtask> subtaskMap = new HashMap<>();
    private final Map<Long, Epic> epicMap = new HashMap<>();

    public List<Task> getAllTasks() {
        return taskMap.values().stream().toList();
    }

    public List<Subtask> getAllSubtasks() {
        return subtaskMap.values().stream().toList();
    }

    public List<Epic> getAllEpics() {
        return epicMap.values().stream().toList();
    }

    public Task createNewTask(Task task) {
        long taskId = ++TASK_ID_COUNTER;
        task.setId(taskId);
        if (task instanceof Subtask subtask) {
            subtaskMap.put(taskId, subtask);
            var epic = subtask.getEpic();
            epic.getSubtasks().add(subtask);
            epic.calculateEpicStatus();
            return subtask;
        } else if (task instanceof Epic epic) {
            epicMap.put(taskId, epic);
            return epic;
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

    public List<Subtask> getEpicSubtasks(long epicId) {
        return Optional.ofNullable(epicMap.getOrDefault(epicId, null))
                .map(Epic::getSubtasks)
                .orElseThrow(() -> new RuntimeException("Epic with epicId = %d doesn't not exist".formatted(epicId)));
    }


    public void updateTask(Task updatedTask) {
        long taskId = updatedTask.getId();
        if (taskMap.containsKey(taskId)) {
            taskMap.put(taskId, updatedTask);
        } else if (subtaskMap.containsKey(taskId)) {
            Subtask updatedSubtask = (Subtask) updatedTask;
            subtaskMap.put(taskId, updatedSubtask);
            var epic = updatedSubtask.getEpic();
            epic.updateSubtasks(updatedSubtask);
            epic.calculateEpicStatus();
        } else if (epicMap.containsKey(taskId)) {
            Epic updatedEpic = (Epic) updatedTask;
            updatedEpic.calculateEpicStatus();
            epicMap.put(taskId, updatedEpic);
        } else {
            throw new RuntimeException("Task with id = %d doesn't not exist".formatted(taskId));
        }
    }

    public void removeById(long taskId) {
        if (taskMap.containsKey(taskId)) {
            taskMap.remove(taskId);
        } else if (subtaskMap.containsKey(taskId)) {
            removeSubtask(taskId);
        } else if (epicMap.containsKey(taskId)) {
            removeEpic(taskId);
        } else {
            throw new RuntimeException("Task with id = %d doesn't not exist".formatted(taskId));
        }
    }

    public void removeSubtask(long subtaskId) {
        var subtask = subtaskMap.get(subtaskId);
        subtaskMap.remove(subtaskId);
        var epic = subtask.getEpic();
        epic.getSubtasks().remove(subtask);
        epic.calculateEpicStatus();
    }

    public void removeEpic(long epicId) {
        List<Long> subtasksToBeDeleted = epicMap.get(epicId).getSubtasks().stream().map(Task::getId).toList();
        epicMap.remove(epicId);
        for (Long subtaskId : subtasksToBeDeleted) {
            subtaskMap.remove(subtaskId);
        }
    }

    public void removeTasks() {
        taskMap.clear();
    }

    public void removeSubtasks() {
        Set<Long> subtaskIds = subtaskMap.keySet();
        for (Long subtaskId : subtaskIds) {
            removeSubtask(subtaskId);
        }
    }

    public void removeEpics() {
        Set<Long> epicIds = epicMap.keySet();
        for (Long epicId : epicIds) {
            removeEpic(epicId);
        }
    }

    public void removeAllTasks() {
        taskMap.clear();
        subtaskMap.clear();
        epicMap.clear();
    }
}
