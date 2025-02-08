package manager;

import exception.ManagerSaveException;
import exception.NotFoundException;
import history.HistoryManager;
import models.Epic;
import models.Subtask;
import models.Task;
import utils.Managers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;


public class InMemoryTaskManager implements TaskManager {
    private static long TASK_ID_COUNTER = 0;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final Map<Long, Task> taskMap = new HashMap<>();
    private final Map<Long, Subtask> subtaskMap = new HashMap<>();
    private final Map<Long, Epic> epicMap = new HashMap<>();

    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public List<Task> getAllTasks() {
        return taskMap.values().stream().toList();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return subtaskMap.values().stream().toList();
    }

    @Override
    public List<Epic> getAllEpics() {
        return epicMap.values().stream().toList();
    }

    @Override
    public Task createNewTask(Task task) {
        long taskId = ++TASK_ID_COUNTER;
        task.setId(taskId);

        if (task instanceof Subtask subtask) {
            subtaskMap.put(taskId, subtask);
            var epic = subtask.getEpic();
            epic.getSubtasks().add(subtask);
            epic.calculateEpic();

            validateTimeCrossing(subtask);
            addPriorityTask(subtask);
            return subtask;
        } else if (task instanceof Epic epic) {
            epicMap.put(taskId, epic);
            return epic;
        } else {
            taskMap.put(taskId, task);

            validateTimeCrossing(task);
            addPriorityTask(task);
            return task;
        }
    }

    @Override
    public void validateTimeCrossing(Task newtask) {
        getPrioritizedTasks().stream()
                .filter(overlappingWith(newtask))
                .findAny()
                .ifPresent(crossingTask -> {
                    throw new ManagerSaveException("New Task with ID = %d is crossing existing task with ID = %d"
                            .formatted(newtask.getId(), crossingTask.getId()));
                });
    }

    public static Predicate<Task> overlappingWith(Task newtask) {
        return existingTask ->
                !existingTask.getStartTime().isAfter(newtask.getEndTime())
                        && !existingTask.getEndTime().isBefore(newtask.getStartTime());
    }

    private void addPriorityTask(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public Task getById(long taskId) {
        Task task = getTask(taskId);

        if (task == null) {
            task = getSubtask(taskId);
        }

        if (task == null) {
            task = Optional.ofNullable(getEpic(taskId))
                    .orElseThrow(() ->
                            new RuntimeException("Task with taskId = %d doesn't not exist".formatted(taskId)));
        }
        return task;
    }

    @Override
    public List<Subtask> getEpicSubtasks(long epicId) {
        return Optional.ofNullable(epicMap.getOrDefault(epicId, null))
                .map(Epic::getSubtasks)
                .orElseThrow(() -> new RuntimeException("Epic with epicId = %d doesn't not exist".formatted(epicId)));
    }


    @Override
    public void updateTask(Task updatedTask) {
        long taskId = updatedTask.getId();
        if (taskMap.containsKey(taskId)) {
            Optional.ofNullable(taskMap.get(taskId))
                    .ifPresent(prioritizedTasks::remove);
            taskMap.put(taskId, updatedTask);
            validateTimeCrossing(updatedTask);
            addPriorityTask(updatedTask);
        } else if (subtaskMap.containsKey(taskId)) {
            Subtask updatedSubtask = (Subtask) updatedTask;
            Optional.ofNullable(subtaskMap.get(taskId))
                    .ifPresent(prioritizedTasks::remove);
            subtaskMap.put(taskId, updatedSubtask);
            var epic = updatedSubtask.getEpic();
            epic.updateSubtasks(updatedSubtask);
            validateTimeCrossing(updatedSubtask);
            addPriorityTask(updatedSubtask);
            epic.calculateEpic();
        } else if (epicMap.containsKey(taskId)) {
            Epic updatedEpic = (Epic) updatedTask;
            updatedEpic.calculateEpic();
            epicMap.put(taskId, updatedEpic);
        } else {
            throw new RuntimeException("Task with id = %d doesn't not exist".formatted(taskId));
        }
    }

    @Override
    public void removeById(long taskId) {
        if (taskMap.containsKey(taskId)) {
            removeTask(taskId);
        } else if (subtaskMap.containsKey(taskId)) {
            removeSubtask(taskId);
        } else if (epicMap.containsKey(taskId)) {
            removeEpic(taskId);
        } else {
            throw new RuntimeException("Task with id = %d doesn't not exist".formatted(taskId));
        }
    }

    @Override
    public Task getTask(long taskId) {
        return Optional.ofNullable(taskMap.get(taskId))
                .map(task -> {
                    historyManager.add(task);
                    return task;
                })
                .orElseThrow(NotFoundException::new);

    }

    @Override
    public Subtask getSubtask(long subtaskId) {
        return Optional.ofNullable(subtaskMap.get(subtaskId))
                .map(subtask -> {
                    historyManager.add(subtask);
                    return subtask;
                })
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Epic getEpic(long epicId) {
        return Optional.ofNullable(epicMap.get(epicId))
                .map(epic -> {
                    historyManager.add(epic);
                    return epic;
                })
                .orElseThrow(NotFoundException::new);
    }

    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void resetCounter() {
        TASK_ID_COUNTER = 0;
    }

    public void removeTask(long taskId) {
        Optional.ofNullable(taskMap.get(taskId))
                .ifPresent(prioritizedTasks::remove);
        taskMap.remove(taskId);
        historyManager.remove(taskId);
    }

    public void removeSubtask(long subtaskId) {
        var subtask = subtaskMap.get(subtaskId);
        prioritizedTasks.remove(subtask);
        subtaskMap.remove(subtaskId);
        historyManager.remove(subtaskId);

        var epic = subtask.getEpic();
        epic.getSubtasks().remove(subtask);
        epic.calculateEpic();
    }

    public void removeEpic(long epicId) {
        List<Long> subtasksToBeDeleted = epicMap.get(epicId).getSubtasks().stream().map(Task::getId).toList();
        epicMap.remove(epicId);
        historyManager.remove(epicId);

        subtasksToBeDeleted.forEach(subtaskId -> {
            Optional.ofNullable(subtaskMap.get(subtaskId))
                    .ifPresent(prioritizedTasks::remove);
            subtaskMap.remove(subtaskId);
            historyManager.remove(subtaskId);
        });
    }

    public void removeTasks() {
        taskMap.keySet().forEach(this::removeTask);
    }

    public void removeSubtasks() {
        subtaskMap.keySet().forEach(this::removeSubtask);
    }

    public void removeEpics() {
        epicMap.keySet().forEach(this::removeEpic);
    }

    public void removeAllTasks() {
        taskMap.clear();
        subtaskMap.clear();
        epicMap.clear();
        historyManager.removeAllHistory();
        prioritizedTasks.clear();
    }
}
