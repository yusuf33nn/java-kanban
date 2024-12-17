package manager;

import exception.ManagerSaveException;
import models.Task;

import java.io.IOException;
import java.nio.file.Files;
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
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
                Files.writeString(path, "id,type,name,status,description,epic\n");
            } catch (IOException e) {
                throw new ManagerSaveException("Error while creating new csv file");
            }
        }
        this.getAllTasks();

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

    private String toString(Task task) {
        //TODO здесь через instanceOf?
        return null;
    }

    private Task fromString(String value) {
        //TODO а этот метод как раз, чтобы превратить строку из csv в объект Task и добавить его в мапу?
        return null;
    }

}
