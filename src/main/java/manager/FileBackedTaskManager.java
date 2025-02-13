package manager;

import enums.TaskStatus;
import exception.ManagerSaveException;
import models.Epic;
import models.Subtask;
import models.Task;
import models.TaskType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path path;

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    @Override
    public Task createNewTask(Task task) {
        var taskId = task.getId();
        var newTask = super.createNewTask(task);
        if (taskId == 0) {
            save();
        }
        return newTask;
    }

    public Path getPath() {
        return path;
    }

    private void save() {
        File file = path.toFile();
        if (!path.toFile().exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new ManagerSaveException("Error creating file: %s with path: '%s'".formatted(e.getMessage(), path.toString()));
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            bw.write("id,type,name,status,description,startTime,duration,epic\n");

            for (Task task : getAllTasks()) {
                bw.write(task.toString(true) + ",\n");
            }

            for (Epic epic : getAllEpics()) {
                bw.write(epic.toString(true) + ",\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                bw.write(subtask.toString(true) + "," + subtask.getEpic().getId() + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Error while saving file: %s".formatted(e.getMessage()));
        }
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void removeTask(long taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeSubtask(long subtaskId) {
        super.removeSubtask(subtaskId);
        save();
    }

    @Override
    public void removeEpic(long epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    private Task fromString(String value) {

        String[] fields = value.split(",");
        var id = Long.parseLong(fields[0]);
        var taskType = TaskType.valueOf(fields[1].toUpperCase());
        var name = fields[2];
        var desc = fields[4];
        var taskStatus = TaskStatus.valueOf(fields[3].toUpperCase());
        var startTime = LocalDateTime.parse(fields[5]);
        var duration = Duration.ofMinutes(Long.parseLong(fields[6]));
        return switch (taskType) {
            case TASK -> new Task(id, name, desc, TaskType.TASK, taskStatus, startTime, duration);
            case SUBTASK -> {
                var epicId = Long.parseLong(fields[7]);
                var epic = this.getEpic(epicId);
                yield new Subtask(id, name, desc, taskStatus, epic, startTime, duration);
            }
            case EPIC -> new Epic(id, name, desc, taskStatus, startTime, duration);
        };
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        var taskManager = new FileBackedTaskManager(file.toPath());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; br.ready(); i++) {
                String line = br.readLine();
                if (i == 0) {
                    continue;
                }
                var task = taskManager.fromString(line);
                taskManager.createNewTask(task);
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
        } catch (Exception e) {
            System.out.println("Произошла непредвиденная ошибка");
            throw e;
        }
        return taskManager;
    }
}
