import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import models.TaskType;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static manager.FileBackedTaskManager.loadFromFile;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = loadFromFile(new File("src/main/resources/tasks.csv"));
        Task task1 = new Task("task1", "task1_desc1", TaskType.TASK, LocalDateTime.now(), Duration.ofMinutes(400));
        taskManager.createNewTask(task1);
        Task task2 = new Task("task2", "task2_desc2", TaskType.TASK, LocalDateTime.now(), Duration.ofMinutes(500));
        taskManager.createNewTask(task2);

        Epic epic1 = new Epic("epic1", "epic1_desc1", LocalDateTime.now(), Duration.ZERO);
        taskManager.createNewTask(epic1);

        Subtask subtask1 = new Subtask("subtask1", "subtask1_desc1", LocalDateTime.now(), Duration.ofMinutes(200));
        subtask1.setEpic(epic1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2_desc2", LocalDateTime.now(), Duration.ofMinutes(300));
        subtask2.setEpic(epic1);
        Subtask subtask3 = new Subtask("subtask3", "subtask3_desc3", LocalDateTime.now(), Duration.ofMinutes(900));
        subtask3.setEpic(epic1);
        taskManager.createNewTask(subtask1);
        taskManager.createNewTask(subtask2);
        taskManager.createNewTask(subtask3);

        Epic epic2 = new Epic("epic2", "epic2_desc2", LocalDateTime.now(), Duration.ZERO);
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
