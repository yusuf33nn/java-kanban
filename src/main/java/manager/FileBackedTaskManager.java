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

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path path;

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    @Override
    public Task createNewTask(Task task) {
        var newTask = super.createNewTask(task);
        save();
        return newTask;
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toFile(), false))) {
            bw.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                bw.write(task.toString() + ",\n");
            }

            for (Epic epic : getAllEpics()) {
                bw.write(epic.toString() + ",\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                bw.write(subtask.toString() + "," + subtask.getEpic().getId() + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Error while saving file");
        }
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
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
        var taskId = Long.parseLong(fields[0]);
        var taskType = TaskType.valueOf(fields[1].toUpperCase());
        var taskName = fields[2];
        var taskDesc = fields[4];
        var taskStatus = TaskStatus.valueOf(fields[3].toUpperCase());
        return switch (taskType) {
            case TASK -> new Task(taskId, taskName, taskDesc, TaskType.TASK, taskStatus);
            case SUBTASK -> {
                var epicId = Long.parseLong(fields[5]);
                var epic = this.getEpic(epicId);
                yield new Subtask(taskId, taskName, taskDesc, taskStatus, epic);
            }
            case EPIC -> new Epic(taskId, taskName, taskDesc, taskStatus);
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
