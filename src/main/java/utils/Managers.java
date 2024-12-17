package utils;

import history.HistoryManager;
import history.InMemoryHistoryManager;
import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;

import java.io.File;

public class Managers {

    private Managers() {
        throw new IllegalStateException("This is an utility Class and cannot be instantiated");
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    /*TODO не понимаю - как состояние мэнеджера надо восстановить?
        Типа файл сохранился, программа перезапустилась и надо засунуть все таски по всем мапам?
    * */
    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file.toPath());
    }
}
