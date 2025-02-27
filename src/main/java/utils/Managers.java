package utils;

import history.HistoryManager;
import history.InMemoryHistoryManager;
import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;

import java.nio.file.Path;

public class Managers {

    private Managers() {
        throw new IllegalStateException("This is an utility Class and cannot be instantiated");
    }

    public static TaskManager getDefault() {
        Path path = Path.of("src/main/resources/tasks.csv");

        if (path.toFile().exists()) {
            return FileBackedTaskManager.loadFromFile(path.toFile());
        } else {
            return new FileBackedTaskManager(path);
        }
    }

    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
