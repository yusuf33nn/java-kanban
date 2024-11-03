package manager;

import models.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private static long TASK_ID_COUNTER = 0;

    private final Map<Long, Task> taskMap = new HashMap<>();

    public List<Task> getAllTasks() {
        return taskMap.values().stream().toList();
    }

    public Task createNewTask(Task task) {
        task.setId(TASK_ID_COUNTER++);
        return task;
    }

    public Task getById(long id) {
        return taskMap.getOrDefault(id, null);
    }


    public void updateTask(Task updatedTask) {
        if (taskMap.containsKey(updatedTask.getId())) {
            taskMap.put(updatedTask.getId(), updatedTask);
        }
    }

    public void removeById(long id) {
        taskMap.remove(id);
    }

    public void removeAllTasks() {
        taskMap.clear();
    }
}
