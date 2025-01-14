package models;

import enums.TaskStatus;
import manager.TaskManager;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static enums.TaskStatus.DONE;
import static enums.TaskStatus.IN_PROGRESS;
import static enums.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Managers.getInMemoryTaskManager;

class EpicStatusTest {

    private final TaskManager taskManager = getInMemoryTaskManager();

    @ParameterizedTest
    @MethodSource("listOfStatuses")
    void calculateEpicStatus(List<TaskStatus> list) {
        Epic epic = new Epic("epic1", "epic1_desc1", LocalDateTime.now(), Duration.ZERO);
        taskManager.createNewTask(epic);

        Subtask subtask1 = new Subtask("subtask1", "subtask1_desc1", LocalDateTime.now(), Duration.ofMinutes(100));
        subtask1.setEpic(epic);
        subtask1.setStatus(list.getFirst());
        Subtask subtask2 = new Subtask("subtask2", "subtask2_desc2", LocalDateTime.now().plusDays(2), Duration.ofMinutes(200));
        subtask2.setEpic(epic);
        subtask2.setStatus(list.get(1));
        Subtask subtask3 = new Subtask("subtask3", "subtask3_desc3", LocalDateTime.now().plusDays(5), Duration.ofMinutes(900));
        subtask3.setEpic(epic);
        subtask3.setStatus(list.get(2));

        taskManager.createNewTask(subtask1);
        taskManager.createNewTask(subtask2);
        taskManager.createNewTask(subtask3);

        epic.calculateEpic();

        assertEquals(list.getLast(), epic.getStatus());
    }

    static Stream<List<TaskStatus>> listOfStatuses() {
        return Stream.of(
                List.of(NEW, IN_PROGRESS, DONE, IN_PROGRESS),
                List.of(NEW, NEW, NEW, NEW),
                List.of(DONE, DONE, DONE, DONE),
                List.of(NEW, DONE, DONE, IN_PROGRESS),
                List.of(IN_PROGRESS, IN_PROGRESS, IN_PROGRESS, IN_PROGRESS)
        );
    }
}
