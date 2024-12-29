package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void tasks_are_equal_if_their_ids_are_equal() {
        Task task1 = new Task("task1", "desc1", TaskType.TASK);
        task1.setId(5L);
        Task task2 = new Task("task2", "desc2", TaskType.TASK);
        task2.setId(5L);

        assertEquals(task1, task2);
    }

    @Test
    void tasks_are_not_equal_if_their_ids_are_not_equal() {
        Task task1 = new Task("task1", "desc1", TaskType.TASK);
        task1.setId(5L);
        Task task2 = new Task("task2", "desc2", TaskType.TASK);
        task2.setId(6L);

        assertNotEquals(task1, task2);
    }

    @Test
    void subtasks_are_equal_if_their_ids_are_equal() {
        Subtask subtask1 = new Subtask("subtask1", "desc1");
        subtask1.setId(10L);
        Subtask subtask2 = new Subtask("subtask2", "desc2");
        subtask2.setId(10L);

        assertEquals(subtask1, subtask2);
    }

    @Test
    void subtasks_are_not_equal_if_their_ids_are_not_equal() {
        Subtask subtask1 = new Subtask("subtask1", "desc1");
        subtask1.setId(10L);
        Subtask subtask2 = new Subtask("subtask2", "desc2");
        subtask2.setId(20L);

        assertNotEquals(subtask1, subtask2);
    }

    @Test
    void epics_are_equal_if_their_ids_are_equal() {
        Epic epic1 = new Epic("epic1", "desc1");
        epic1.setId(100L);
        Epic epic2 = new Epic("epic2", "desc2");
        epic2.setId(100L);

        assertEquals(epic1, epic2);
    }

    @Test
    void epics_are_not_equal_if_their_ids_are_not_equal() {
        Epic epic1 = new Epic("epic1", "desc1");
        epic1.setId(100L);
        Epic epic2 = new Epic("epic2", "desc2");
        epic2.setId(101L);

        assertNotEquals(epic1, epic2);
    }
}