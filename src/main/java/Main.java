import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import models.TaskType;

import java.io.File;

import static manager.FileBackedTaskManager.loadFromFile;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = loadFromFile(new File("src/main/resources/tasks.csv"));
        Task task1 = new Task("task1", "task1_desc1", TaskType.TASK);
        taskManager.createNewTask(task1);
        Task task2 = new Task("task2", "task2_desc2", TaskType.TASK);
        taskManager.createNewTask(task2);

        Epic epic1 = new Epic("epic1", "epic1_desc1");
        taskManager.createNewTask(epic1);

        Subtask subtask1 = new Subtask("subtask1", "subtask1_desc1");
        subtask1.setEpic(epic1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2_desc2");
        subtask2.setEpic(epic1);
        Subtask subtask3 = new Subtask("subtask3", "subtask3_desc3");
        subtask3.setEpic(epic1);
        taskManager.createNewTask(subtask1);
        taskManager.createNewTask(subtask2);
        taskManager.createNewTask(subtask3);

        Epic epic2 = new Epic("epic2", "epic2_desc2");
        taskManager.createNewTask(epic2);

        taskManager.getById(3L);
        taskManager.getById(1L);
        taskManager.getById(4L);
        taskManager.getById(1L);
        taskManager.getById(2L);
        taskManager.getById(5L);
        taskManager.getById(2L);
        taskManager.getById(7L);
        taskManager.getById(6L);
        taskManager.getById(7L);

        taskManager.getHistoryManager()
                .getHistory()
                .forEach(System.out::println);
        System.out.println("=========================");
        System.out.println("=========================");
        System.out.println("=========================");
        System.out.println("=========================");
        System.out.println("=========================");

        taskManager.removeById(2);

        taskManager.getHistoryManager()
                .getHistory()
                .forEach(System.out::println);

        System.out.println("=========================");
        System.out.println("=========================");
        System.out.println("=========================");
        System.out.println("=========================");
        System.out.println("=========================");

        taskManager.removeById(epic1.getId());

        taskManager.getHistoryManager()
                .getHistory()
                .forEach(System.out::println);
    }
}
